#!/bin/bash

# sends 20 sbatch runs
# should be run only from its directory!

orig_dir=$PWD
regression_dir=../regressions/`basename $PWD`/`date "+%F_%H-%M-%S"`
mkdir -p $regression_dir
./info.sh > $regression_dir/info.txt

for i in {1..20}
do
    mkdir $regression_dir/run_$i
    cd $regression_dir/run_$i
    pwd
    if [ "$i" == "1" ]; then
        # send only first one with mail at end, or problems
        sbatch --mail-type=END,FAIL,REQUEU $orig_dir/run.sh $orig_dir
    else
        sbatch $orig_dir/run.sh $orig_dir
    fi
    cd $orig_dir
done

