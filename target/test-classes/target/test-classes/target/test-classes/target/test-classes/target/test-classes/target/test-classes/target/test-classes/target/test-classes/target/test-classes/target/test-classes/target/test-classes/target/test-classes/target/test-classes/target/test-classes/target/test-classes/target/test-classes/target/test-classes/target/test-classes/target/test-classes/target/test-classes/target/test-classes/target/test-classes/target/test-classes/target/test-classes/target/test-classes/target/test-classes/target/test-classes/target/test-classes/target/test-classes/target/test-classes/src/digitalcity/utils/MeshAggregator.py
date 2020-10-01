import os
import time
import pandas as pd


def read_dataset(file_path, new_columns=[]):
    if new_columns:
        dataset = pd.read_csv(file_path, names=new_columns, header=0)
    else:
        dataset = pd.read_csv(file_path)

    return dataset


def aggregation(trajectory_dataset, path, filename):
    """ aggregate the trajectory to get the distribution data
    """
    result = pd.DataFrame(columns=['meshid', 'hour', 'population'])
    grouped = trajectory_dataset.groupby(['meshid', 'hour'])

    for group in grouped:

        result = result.append({'meshid': group[0][0], 'hour': group[0][1], 'population': len(group[1])},
                               ignore_index=True)

    result.to_csv(path + filename[:-4] + '_aggregation.csv')


def main():
    """ main function of the aggregation
    """

    src_path = '/home/ubuntu/Projects/DigitalCityProject/Output/20200928.43751/mesh/'
    new_columns = ['agentid', 'hour', 'meshid']

    for filename in os.listdir(src_path):
        print(filename)
        trajectory_dataset = read_dataset(src_path + filename, new_columns)
        aggregation(trajectory_dataset, src_path, filename)


if __name__ == "__main__":
    main()
