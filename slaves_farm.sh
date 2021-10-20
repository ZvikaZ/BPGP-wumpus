#!/bin/bash

# sends slaves
# should be run only from its directory!

for i in {1..50}
do
   sbatch ./run_slave.sh
done

