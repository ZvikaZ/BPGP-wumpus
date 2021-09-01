// returns true if cells a,b are near each other
function near(a, b) {
    return  (a.row == b.row && ((a.col == b.col + 1) || (a.col == b.col -1))) ||
            (a.col == b.col && ((a.row == b.row + 1) || (a.row == b.row -1)))
}

// what should be the direction of a, in order to move forward to b, assuming near(a,b)
function direction(a, b) {
    if (a.row < b.row)
        return 0
    else if (a.row > b.row)
        return 180
    else if (a.col < b.col)
        return 90
    else
        return 270
}

// return plan from player to dest, assuming near(player,dest)
function planToNear(dest) {
    let player = ctx.getEntityById("player")
    return planFromAnyToNear(player, dest).plan
}

// return plan from src to dest, assuming near(src,dest) - regardless of player's current location
function planFromAnyToNear(src, dest) {
    if (!near(src, dest)) {
        bp.log.info(src)
        bp.log.info(dest)
        throw new Error("planToNear: src is not near " + dest.row + "," + dest.col + "! src is at: " + src.row + "," + src.col)
    }

    let dir = direction(src, dest)
    let delta = dir - src.facing
    if (delta < 0)
        delta += 360
    let result = []
    if (delta == 0) {
        // pass
    } else if (delta == 90) {
        result.push('turn-right')
    } else if (delta == 180) {
        result.push('turn-right')
        result.push('turn-right')
    } else if (delta == 270) {
        result.push('turn-left')
    } else {
        throw new Error('unknown delta')
    }
    result.push('forward')
    return {plan: result, direction: dir}
}

function isCellSafe(cell) {
    let cellEntity = getCellFromCtx(cell.row, cell.col)
    return (cellEntity.Pit == "clean" || cellEntity.Pit == "visited") &&
           (cellEntity.Wumpus == "clean" || cellEntity.Wumpus == "visited")
}

// return plan from player to row,col (which can be far)
function createPlanTo(dest) {
    // this function uses a lot of -1 because generally we start row (and col) from 1, while here the
    // array index starts from 0

    if (!isCellSafe(dest))
        return []

    let cells = []
    for (let i = 1; i <= ROWS; i++) {
        cells[i-1] = []
        for (let j = 1; j <= COLS; j++)
            cells[i-1][j-1] = {
                row: i,
                col: j,
                route: null
            }
    }

    // initialize the route from the dest cell to itself to empty
    cells[dest.row-1][dest.col-1].route = []

    let fringe = [cells[dest.row-1][dest.col-1]]
    while (fringe.length > 0) {
        let current = fringe.pop()
        bp.log.fine(current)
        let nearCells = getNearCells(current, function (row, col) {
            return cells[row-1][col-1]
        })
        for (let i = 0; i < nearCells.length; i++) {
            let cell = nearCells[i] //.slice()     // '.slice()' makes a copy
            if (isCellSafe(cell) && current.route != null) {
                let route = current.route.concat([{row: current.row, col: current.col}])
                if (cell.route == null || route.length < cell.route.length) {
                    cell.route = route
                    fringe.push(cell)
                    cells[cell.row-1][cell.col-1] = cell

                }
            }
        }
    }

    // bp.log.info("CELLS")
    // for (let i = 0; i < 4; i++)
    //     for (let j = 0; j < 4; j++) {
    //         let cell = cells[i][j]
    //         if (cell) {
    //             bp.log.info(cell.row + "," + cell.col + ": ")
    //             bp.log.info(cell.route)
    //         }
    //     }

    let player = ctx.getEntityById("player")
    let route = cells[player.row-1][player.col-1].route
    // now route contains an array of cells - the first is the dest, the last is near player's current location

    let result = []
    if (route != null && route.length > 0)
        for (let i = route.length - 1; i >= 0; i--) {
            let shortPlan = planFromAnyToNear(player, route[i])
            result = result.concat(shortPlan.plan)
            player = route[i]
            player.facing = shortPlan.direction
        }
    return result
}



const AnyPlay = Any('Play')
const AnyPlan = Any('Plan')
const grab = Event("Play", {id: 'grab'})

///////////////////////////////////////////////////////////
///////////            rules                 //////////////
///////////////////////////////////////////////////////////


// required in order to make some initial effects to our kb in dal
bthread("start", function() {
    //TODO here, and in all 'request's - try to remove priority
    sync({request: Event("Start")}, 1000)
})

ctx.bthread("Game over", "Game over", function (entity) {
    bp.log.info("Game over: " + entity.reason + ", score: " + entity.score)
    let ev = Event("Game over", {score: entity.score})
    sync({request: ev, block: ev.negate()})
    sync({block: bp.all})
})


///////////////////////////////////////////////////////////
///////////            printing              //////////////
///////////////////////////////////////////////////////////

bthread("boardPrinter", function() {
    let board = []
    for (var i = 0; i < ROWS; i++) {
        let row = []
        for (var j = 0; j < COLS; j++) {
            row.push('_')
        }
        board.push(row)
    }

    while (true) {
        let player = ctx.getEntityById("player")
        let x = player.row - 1
        let y = player.col - 1
        let facing = player.facing
        if (facing == 0)
            board[x][y] = '^'
        else if (facing == 90)
            board[x][y] = '>'
        else if (facing == 180)
            board[x][y] = 'V'
        else if (facing == 270)
            board[x][y] = '<'

        bp.log.info("--------------------")
        for (var i = ROWS - 1; i >= 0; i--) {
            bp.log.info(board[i].join(''))
        }
        bp.log.info("--------------------")
        sync({waitFor: AnyPlay});
    }
})

///////////////////////////////////////////////////////////
///////////            strategies            //////////////
///////////////////////////////////////////////////////////

// moved 3 main strategies to evolved.js

// random walker, with low prio - to do something when strategies don't care
bthread("player - random walker", function () {
    while(true) {
        // bp.log.info(player.row + ":" + player.col + "," + player.facing + " visited nearby: " + cell.row + ":" + cell.col + ". direction: " + direction(player, cell) + ". plan: " + plan)
        sync({request: [
            Event("Play", {id: 'turn-right'}),
            Event("Play", {id: 'turn-left'}),
            Event("Play", {id: 'forward'})
        ], waitFor: AnyPlay}, 10)
    }
})

//TODO: not 'possible' but certain!
ctx.bthread("Avoid (possible) danger", "Cell.NearPossibleDanger_NoGold_SafeCellExist", function (entity) {
    while(true) {
        let player = ctx.getEntityById("player")
        if (!near(player, entity))
            throw new Error("Avoid (possible) danger BT: player is not near " + entity.row + "," + entity.col + "! he is at: " + player.row + "," + player.col)
        let delta = direction(player, entity) - player.facing
        if (delta == 0) {
            // bp.log.info("BLOCKING forward to cell " + entity.row + "," + entity.col)
            sync({
                block: Event("Play", {id: 'forward'}),
                waitFor: ContextChanged
            })
            // bp.log.info("Stopped blocking forward to cell " + entity.row + "," + entity.col)
        }
        else
            sync({waitFor: AnyPlay})
    }
})

bthread("stop wandering around",  function () {
    let max_actions = (ROWS * COLS) * ROWS * 3  // estimation
    for (let i = 1; i <= max_actions; i++)
        sync({waitFor: AnyPlay})
    sync({request: Event("Wandering")}, 200)
})


// player is in cell with gold - try to take it
ctx.bthread("Grab gold", "PlayerInCellWithGold", function (entity) {
    // the loop is required, because maybe the some other action was selected, and the gold wasn't grabbed
    while (true) {
        //TODO remove prio
        sync({request: grab}, 110)  //Z removing 'block: grab' fixed the infinite loop ; also added back prio
    }
})

// player just took gold - return to beginning, and climb out
ctx.bthread("Escape from cave with gold", "PlayerHasGold", function (kb) {
    //TODO take shortest path, instead of return as we came...
    let plan = createPlanTo({row: 1, col: 1})
    plan.push('climb')
    let p = Event("Plan", {plan: plan})
    sync({request: p, block: p.negate()}, 100)  //Z returned prio
})

//TODO del
bthread("debug climb", function () {
    sync({waitFor: Event("Play", {id: 'climb'})})
    let player = ctx.getEntityById("player")
    bp.log.info(player)
    if (player.row != 1 || player.col != 1)
        throw new Error("Wrong climbing")
})



//TODO when to shoot?

////////////////////////////////////////////

// a utility BT - not candidate for evolution - receives a plan ( = list of action strings), and just executes it, step by step
ctx.bthread("Execute plan", "Active plan", function (entity) {
    let plan = entity.val
    bp.log.info("execute plan, got: " + plan)
    for (var i = 0; i < plan.length; i++) {
        let e = sync({request: Event("Play", {id: plan[i]}), block: AnyPlan}, 140) //Z returned prio
        bp.log.info("Executed " + plan[i] + ", step #" + i + " of plan: " + plan)
        if (e.data.id != plan[i])
            throw new Error("plan execution failure!")
    }
    sync({request: Event("Finished plan")}, 240)  // uncommented this

    let player = ctx.getEntityById("player")
    bp.log.info("Finished executing plan: " + plan + ". Now player in: " + player.row + ":" + player.col)
})