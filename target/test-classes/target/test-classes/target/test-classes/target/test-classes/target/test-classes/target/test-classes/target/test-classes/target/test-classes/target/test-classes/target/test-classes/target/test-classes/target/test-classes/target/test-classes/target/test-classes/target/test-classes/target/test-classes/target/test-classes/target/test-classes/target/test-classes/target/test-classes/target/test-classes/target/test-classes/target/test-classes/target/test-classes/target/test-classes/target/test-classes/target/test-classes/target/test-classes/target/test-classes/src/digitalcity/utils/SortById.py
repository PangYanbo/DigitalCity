import sys
import pandas as pd


def main(filename):

    df = pd.read_csv(filename, names=['pid', 'time', 'lon', 'lat'])
    df = df.sort_values(['pid', 'time'])
    df.to_csv(filename[0:-4]+'_sorted.csv', header=False, index=False)


if __name__ == "__main__":
    main(sys.argv[1])
