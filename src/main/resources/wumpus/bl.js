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

// return plan from player to b, assuming near(player,b)
function planToNear(b) {
    let player = ctx.getEntityById("player")
    if (!near(player, b)) {
        bp.log.info(player)
        bp.log.info(b)
        throw new Error("planToNear: player is not near " + b.row + "," + b.col + "! he is at: " + player.row + "," + player.col)
    }

    let delta = direction(player, b) - player.facing
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
    return result
}

// creates a plan to return to beginning, if we've taken so far actions_historty
function createReversedPlan() {
    let actions_history = ctx.getEntityById("kb").actions_history
    let plan = ['turn-left', 'turn-left']
    for (let i = actions_history.length; i >= 0; i--) {
        let action = actions_history[i]
        if (action == 'turn-right')
            plan.push('turn-left')
        else if (action == 'turn-left')
            plan.push('turn-right')
        else if (action == 'forward')
            plan.push('forward')
    }
    return plan
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

// the following 3 are before player has taken gold:

//these are commented, and should be replaced by evolution:
/*
// go to cells that are near the player, and the player isn't aware of any danger in them
ctx.bthread("player - go to unvisited cell with no known danger", "Cells.NearWithoutKnownDanger_NoGold", function (cell) {
    while(true) {
        let plan = planToNear(cell)
        // bp.log.info(player.row + ":" + player.col + "," + player.facing + " no known danger nearby: " + cell.row + ":" + cell.col + ". direction: " + direction(player, cell) + ". plan: " + plan)
        sync({request: Event("Plan", {plan: plan}), waitFor: bp.all}, 60)
    }
})

// go to cells that are near the player, we haven't visited before, and we are sure that aren't dangerous - be an explorer, but safely
ctx.bthread("player - go to unvisited cell without danger", "Cell.NearUnvisitedNoDanger_NoGold", function (cell) {
    while(true) {
        let plan = planToNear(cell)
        // bp.log.info(player.row + ":" + player.col + "," + player.facing + " clean nearby: " + cell.row + ":" + cell.col + ". direction: " + direction(player, cell) + ". plan: " + plan)
        sync({request: Event("Plan", {plan: plan}), waitFor: bp.all}, 70)
    }
})

// go to cells that are near the player, we have already visited (and therefore, are safe) - boring, but might prove useful to open new frontiers from there
ctx.bthread("player - return to visited cell", "Cell.NearVisited_NoGold", function (cell) {
    while(true) {
        let plan = planToNear(cell)
        // bp.log.info(player.row + ":" + player.col + "," + player.facing + " visited nearby: " + cell.row + ":" + cell.col + ". direction: " + direction(player, cell) + ". plan: " + plan)
        sync({request: Event("Plan", {plan: plan}), waitFor: bp.all}, 50)
    }
})
*/

// note the different priorities of the last 3 BTs:
// player - go to unvisited cell without danger: 70
// player - go to unvisited cell with no known danger: 60
// player - return to visited cell : 50


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
    let plan = createReversedPlan()
    plan.push('climb')
    let p = Event("Plan", {plan: plan})
    sync({request: p, block: p.negate()}, 100)  //Z returned prio
})


//TODO when to shoot?

////////////////////////////////////////////

// a utility BT - not candidate for evolution - receives a plan ( = list of action strings), and just executes it, step by step
ctx.bthread("Execute plan", "Active plan", function (entity) {
    let plan = entity.val
    bp.log.info("execute plan, got: " + plan)
    for (var i = 0; i < plan.length; i++) {
        sync({request: Event("Play", {id: plan[i]}), block: AnyPlan}, 140) //Z returned prio
        bp.log.info("Executed step #" + i + " of plan: " + plan)
    }
    sync({request: Event("Finished plan")}, 240)  // uncommented this

    let player = ctx.getEntityById("player")
    bp.log.info("Finished executing plan: " + plan + ". Now player in: " + player.row + ":" + player.col)
})