#!/home/zvikah/.conda/envs/bpgp/bin/python

# calcs the fitness of golden.js over all boards
# should be run from main directory of project

import subprocess
from pathlib import Path

ROUNDS = 10
NUM_OF_BOARDS = 100
GOLDEN_JS = "golden.js"

total = 0
runs = 0
for k in range(ROUNDS):
    for i in range(1, NUM_OF_BOARDS+1):
        runs += 1
        p = subprocess.run(f'mvn exec:java@bpRun -Dexec.args="{GOLDEN_JS} {i}"', shell=True, capture_output=True)
        output = p.stdout.decode().strip()
        result = float(output.split('\n')[-1])
        total += result
        print(f"{k}, {i}: {result}, fitness so far: {total / runs}")

avg = total / runs
print("=============")
print(avg)
print("Fitness is: ", avg)
