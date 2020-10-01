import os
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
sns.set(style='darkgrid')


def visual_corr(source_dir, source_name, observation, particle_no):
    # record corr to file
    corr = []

    src = pd.read_csv(source_dir+source_name, usecols=[1, 2, 3])
    print(source_dir+source_name)
    print(src)
    obs = pd.read_csv(observation, skiprows=1, usecols=[0, 2, 3, 7],
                      names=['date', 'hour', 'meshid', 'observation'], dtype=float)[['date', 'meshid', 'hour', 'observation']]
    obs = obs.loc[obs['date']==20191013]

    plt.figure(figsize=(9*2+3, 12.5))
    plt.subplots_adjust(left=0.04,right=.98,bottom=.03,top=.98,wspace=.15,hspace=.3)
    plot_num = 1

    for i in range(6, 22):
        name = 'Mesh Pop@'+str(i)+':00'
        plt.subplot(4, 4, i-5)
        plt.title(name, size=12)
        # select data
        src_hourly_mesh_pop = src.loc[src['hour'] == float(i)][['meshid', 'population']]
        obs_hourly_mesh_pop = obs.loc[obs['hour'] == i*100][['meshid', 'observation']]
        # join dataframe
        hourly_mesh_pop = pd.merge(src_hourly_mesh_pop, obs_hourly_mesh_pop, on='meshid', how='left')
        hourly_mesh_pop = hourly_mesh_pop.fillna(0)
        # corr
        print((hourly_mesh_pop['observation'], hourly_mesh_pop['population']))
        coef = np.corrcoef(hourly_mesh_pop['observation'], hourly_mesh_pop['population'])[0][1]
        corr.append(coef)
        label = r'$\rho$='+str(round(coef, 3))
        ax = plt.gca()
        ax.annotate(label, xy=(0.05, 0.85), size=14, xycoords=ax.transAxes)
        plt.scatter(hourly_mesh_pop['observation'], hourly_mesh_pop['population'], s=3)

    np.savetxt(source_dir+particle_no+'_corr.csv', np.array(corr))
    plt.savefig('/home/ubuntu/Projects/DigitalCityProject/Output/20201001./particle5.png')


if __name__ == '__main__':
    src_dir = '/home/ubuntu/Projects/DigitalCityProject/Output/20201001.2/mesh/'
    obs_path = '/home/ubuntu/Projects/DigitalCityProject/Data/DocomoMeshData/Susono_Nanto.csv'

    for src in os.listdir(src_dir):
        visual_corr(src_dir, src, obs_path, src.split('.')[0])
