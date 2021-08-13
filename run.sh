#!/bin/bash
#SBATCH --job-name BpgpEvolve
#SBATCH --mail-user=zvikah@post.bgu.ac.il
##SBATCH --mail-type=END,FAIL,REQUEU

#SBATCH --cpus-per-task=8 # 8 cpus per task - use for multithreading, usually with --tasks=1
#SBATCH --tasks=1 # 1 processes - use for multiprocessing

# required to support both simple runs from bash (without parameters), and SLURM from regressions.sh
if [ $# -eq 0 ]
then
    TARGET_CLASSES_BASE=`pwd`
else
    TARGET_CLASSES_BASE=$1
fi

time $JAVA_HOME/bin/java -Dfile.encoding=UTF-8 -classpath $TARGET_CLASSES_BASE/target/classes:$HOME/.m2/repository/com/github/ZvikaZ/ecj/27.1-zvika-4/ecj-27.1-zvika-4.jar:$HOME/.m2/repository/com/github/bThink-BGU/BPjs-Context/0.2.5/BPjs-Context-0.2.5.jar:$HOME/.m2/repository/com/github/bthink-bgu/BPjs/0.12.1-modified-quals/BPjs-0.12.1-modified-quals.jar:$HOME/.m2/repository/commons-io/commons-io/2.5/commons-io-2.5.jar BpgpEvolve


