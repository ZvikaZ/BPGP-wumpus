#!/bin/bash
#SBATCH --job-name BpgpSlave
##SBATCH --mail-user=zvikah@post.bgu.ac.il
##SBATCH --mail-type=END,FAIL,REQUEU

###SBATCH --cpus-per-task=64 # 64 cpus per task - use for multithreading, usually with --tasks=1
#SBATCH --tasks=1 # 1 processes - use for multiprocessing

time mvn exec:java@slave
