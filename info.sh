pwd
git rev-parse HEAD
grep generations src/main/resources/*params | grep -v '#'
grep '\.size' src/main/resources/*params  | grep subpop
grep numOfRandomRuns src/main/java/FirstProblem.java | grep final
