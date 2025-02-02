import logging
import math
import random
import time
from os import path

from numpy import mean, std

from copy import deepcopy

import show
from constants import Constants
from utils.csv.csv_reader import read_csv
from utils.json.json_reader import read_json
from utils.yaml.SettingsManager import load_yaml_file


class DataProcessing:
    initial_dimension_flow: None
    __numberExamples = 0

    def __init__(self):
        self.data = load_yaml_file(Constants.yaml_file)
        self.json_data = read_json(self.data[Constants.YamlNames.app][Constants.YamlNames.initTransportSystemFile])
        self.csv_data_path = self.json_data[Constants.JsonNames.output_data_path]
        self.output_data_path = self.json_data[Constants.JsonNames.output_data_path]
        self.csv_data = read_csv(self.csv_data_path)
        self.plot_parameters = self.json_data[Constants.JsonNames.plot_parameters]
        self.plot_structure = self.json_data[Constants.JsonNames.plot_structure]

    def data_shows(self):
        for graph in self.plot_structure:
            output_directory_path = path.dirname(self.csv_data_path) + "\\" + graph[Constants.JsonNames.path]
            x_column = graph[Constants.JsonNames.x_column]
            y_columns = graph[Constants.JsonNames.y_columns]
            values = []
            self.add_column_values(values, x_column)
            for y_column in y_columns:
                self.add_column_values(values, y_column)
            plot_name = graph[Constants.JsonNames.plot_template][Constants.JsonNames.plot_name]
            file_name = graph[Constants.JsonNames.file_name]
            correct_json_data = deepcopy(self.json_data)
            self.override_papameters(correct_json_data, graph, plot_name)

            self.data_show(correct_json_data, output_directory_path, file_name, plot_name, values)

    def override_papameters(self, correct_json_data, graph, plot_name):
        overridden_parameters = graph[Constants.JsonNames.plot_template].get(Constants.JsonNames.plot_parameters, None)
        if overridden_parameters is not None:
            for key, value in overridden_parameters.items():
                if key in correct_json_data[Constants.JsonNames.plot_parameters][plot_name]:
                    correct_json_data[Constants.JsonNames.plot_parameters][plot_name][key] = value

    def add_column_values(self, values, x_column):
        for column_name in self.csv_data.columns.values.tolist():
            if column_name.strip() == x_column.strip():
                values.append(self.csv_data[column_name].values)

    def data_show(self, json_data, path, file_name, plot_name, plot_values):
        show.common_line(json_data, path, plot_values, file_name, plot_name)
