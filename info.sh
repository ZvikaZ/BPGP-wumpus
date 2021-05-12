pwd
git rev-parse HEAD
grep generations src/main/resources/*params | grep -v '#'
grep '\.size' src/main/resources/*params  | grep subpop
grep numOfRandomRuns src/main/java/*Problem.java | grep final
grep 'static Rival rival' src/main/java/*Problem.java