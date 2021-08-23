import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class BpgpProblemTest {

    static final int numOfBoards = 6;       //TODO increase

    String goldenCode = "\n" +
            "// go to cells that are near the player, and the player isn't aware of any danger in them\n" +
            "ctx.bthread(\"player - go to unvisited cell with no known danger\", \"Cell.NearWithoutKnownDanger_NoGold\", function (cell) {\n" +
            "    while(true) {\n" +
            "        let plan = planToNear(cell)\n" +
            "        // bp.log.info(player.row + \":\" + player.col + \",\" + player.facing + \" no known danger nearby: \" + cell.row + \":\" + cell.col + \". direction: \" + direction(player, cell) + \". plan: \" + plan)\n" +
            "        sync({request: Event(\"Plan\", {plan: plan}), waitFor: ContextChanged}, 60)\n" +
            "    }\n" +
            "})\n" +
            "\n" +
            "// go to cells that are near the player, we haven't visited before, and we are sure that aren't dangerous - be an explorer, but safely\n" +
            "ctx.bthread(\"player - go to unvisited cell without danger\", \"Cell.NearUnvisitedNoDanger_NoGold\", function (cell) {\n" +
            "    while(true) {\n" +
            "        let plan = planToNear(cell)\n" +
            "        // bp.log.info(player.row + \":\" + player.col + \",\" + player.facing + \" clean nearby: \" + cell.row + \":\" + cell.col + \". direction: \" + direction(player, cell) + \". plan: \" + plan)\n" +
            "        sync({request: Event(\"Plan\", {plan: plan}), waitFor: ContextChanged}, 70)\n" +
            "    }\n" +
            "})\n" +
            "\n" +
            "// go to cells that are near the player, we have already visited (and therefore, are safe) - boring, but might prove useful to open new frontiers from there\n" +
            "ctx.bthread(\"player - return to visited cell\", \"Cell.NearVisited_NoGold\", function (cell) {\n" +
            "    while(true) {\n" +
            "        let plan = planToNear(cell)\n" +
            "        // bp.log.info(player.row + \":\" + player.col + \",\" + player.facing + \" visited nearby: \" + cell.row + \":\" + cell.col + \". direction: \" + direction(player, cell) + \". plan: \" + plan)\n" +
            "        sync({request: Event(\"Plan\", {plan: plan}), waitFor: ContextChanged}, 50)\n" +
            "    }\n" +
            "})\n";

    BpgpProblem problem;

    @BeforeEach
    void setUp() {
        problem = new BpgpProblem();
    }

    // it's *not* really a test! I'm jusing the framework to check the "golden" fitness -
    // i.e., the fitness of the manually written code before evolution
    @org.junit.jupiter.api.Test
    void bpRun() {
        double totalRunResults = 0;

        for (int i=1; i <= numOfBoards; i++) {
            double runResult = problem.bpRun(goldenCode, i);
            System.out.println("****: " + i + ", runResult:" + runResult);
            totalRunResults += runResult;
        }

        System.out.println(totalRunResults / numOfBoards);
    }

}