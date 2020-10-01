import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
sns.set(style='darkgrid')


def visual_corr(source, observation):
    src = pd.read_csv(source, usecols=[1, 2, 3])
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
        label = r'$\rho$='+str(round(coef, 3))
        ax = plt.gca()
        ax.annotate(label, xy=(0.05, 0.85), size=14, xycoords=ax.transAxes)
        plt.scatter(hourly_mesh_pop['observation'], hourly_mesh_pop['population'], s=3)

    # fig.tight_layout()
    plt.savefig('/home/ubuntu/Projects/DigitalCityProject/Output/20200928.43751/particle5.png')


visual_corr('/home/ubuntu/Projects/DigitalCityProject/Output/20200928.43751/mesh/traj_5.csv_mesh_aggregation.csv',
            '/home/ubuntu/Projects/DigitalCityProject/Data/DocomoMeshData/Susono_Nanto.csv')
