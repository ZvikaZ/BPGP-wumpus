import argparse
import os
import re
from matplotlib import pyplot as plt

STAT_FILE = 'out.stat'

def get_dirs(path):
    return [os.path.join(path, d) for d in os.listdir(path) if os.path.isdir(os.path.join(path, d))]


def analyze_run(d):
    with open(os.path.join(d, STAT_FILE)) as f:
        lines = f.readlines()
    with open(os.path.join(d, STAT_FILE)) as f:
        data = f.read()
    if "Best Individual's code" not in data:
        # not finished
        return
    fitness_lines = [l for l in lines if "Fitness" in l]
    r = re.compile(r'Fitness: Standardized=(\d+\.\d+) Adjusted=\d+\.\d+ Hits=0')
    fitnesses = [float(r.match(l).group(1)) for l in fitness_lines]
    assert len(fitnesses) == len(fitness_lines)
    return {
        'run': d,
        'fitnesses': fitnesses[:-1],
        'best': fitnesses[-1]
    }


def analyze_regression(regression_dir):
    results = []
    for d in get_dirs(regression_dir):
        results.append(analyze_run(d))
    plt.figure()
    results_to_plot = [r for r in results if r is not None]
    # TODO show more than 4?
    results_to_plot = results_to_plot[:4]
    for (index, r) in enumerate(results_to_plot):
        fitnesses = r['fitnesses']
        # TODO the label isn't shown
        plt.subplot(len(results_to_plot), 1, index+1, label=r['run'])
        plt.scatter(range(len(fitnesses)), fitnesses, s=2)
        plt.xlabel("Generation")
        plt.ylabel("Best fitness")
    plt.show()



if __name__ == "__main__":
    parser = argparse.ArgumentParser(formatter_class=argparse.ArgumentDefaultsHelpFormatter,
                                     description="Analyze regression results",)
    parser.add_argument("regression_dir", help="directory containing regression")
    args = parser.parse_args()

    analyze_regression(args.regression_dir)
