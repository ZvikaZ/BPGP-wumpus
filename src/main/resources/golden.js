// these were manually written, and are considered "golden model"
// they aren't used anywere in the code!
// their fitness is only calculated for reference, outside of the regular run flow

ctx.bthread("player - go to safe, unvisited, cell", "Cell.UnVisitedSafeToVisit", function (cell) {
    while(true) {
        let plan = createPlanTo(cell)
        let e = sync({request: Event("Plan", {plan: plan}), waitFor: ContextChanged}, 80 - manhattanDistanceFromPlayer(cell))
        // bp.log.info("go to safe: " + cell.row + ":" + cell.col + ". executed plan: " + plan)
    }
})

ctx.bthread("player - go to possibly dangerous, unvisited, cell", "Cell.UnVisitedPossibleDangerRoute", function (cell) {
    while(true) {
        let plan = createPlanTo(cell)
        let e = sync({request: Event("Plan", {plan: plan}), waitFor: ContextChanged}, 70)
        // bp.log.info("go to possible danger: " + cell.row + ":" + cell.col + ". executed plan: " + plan)
    }
})




