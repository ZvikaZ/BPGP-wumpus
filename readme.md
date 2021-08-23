RUNNING
=======
- `mvn clean package`
- and now, either `run.sh`, or `run.bat`, according to OS


GRAMMAR
=======
```
<start> ::= <bt> <more-bts>
<more-bts> ::= <bt> <more-bts> | <bt>

<bt> ::= ctx.bthread(<name>, "<query>", function (entity) {
    while(true) {
        <request_plan>
    }
})

<name> ::= "arbitrary string"
<query> ::= Cell.NearVisited_NoGold | Cell.NearUnvisitedNoDanger_NoGold | Cell.NearVisited_NoGold
<request_plan> ::= sync({request: Event("Plan", {plan: <planner>}), waitFor: ContextChanged}, <prio>)
<planner> ::= planToNear(entity) 
<prio> ::= 50 | 51 | ... | 69 | 70
```

OLD TODOS
====

NEXT
----
- improve grammar
- geInd.printIndividualForHumans: print also generated code
- sensible initialization
- use logging, and 'fatal' instead of exit


LATER
-----
- stats: print for ind if mutated
- stats: elite - print parent
- bpjs verification?
- coevolution of both sides
- play against preknown good program
- report breedthreads bug
- some explanations in this file...
- clean ecj warnings
- unit tests?
- parallelize with ECJ ch. 6
- better termination?
