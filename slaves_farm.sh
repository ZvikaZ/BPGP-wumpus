#!/bin/bash

# sends slaves
# should be run only from its directory!

for i in {1..250}
do
   sbatch ./run_slave.sh `hostname -i`
done

