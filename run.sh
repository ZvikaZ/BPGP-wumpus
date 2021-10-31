#!/bin/bash
#SBATCH --job-name BpgpEvolve
#SBATCH --mail-user=zvikah@post.bgu.ac.il
##SBATCH --mail-type=END,FAIL,REQUEU

#SBATCH --cpus-per-task=64 # cpus per task - use for multithreading, usually with --tasks=1
#SBATCH --tasks=1 # 1 processes - use for multiprocessing
###SBATCH --mem=4G				### amount of RAM memory

# mem*cpus <= RealMemory (from scontrol)
mvn exec:java@single
