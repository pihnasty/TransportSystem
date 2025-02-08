import sys
import DataProcessing as data_processing
from constants import Constants
from utils.yaml.SettingsManager import load_yaml_file

#
# from pom.stochastic03.Dimensionless.ApproximateType import ApproximateType
# from pom.stochastic03.Dimensionless.DimensionlessType import DimensionlessType

# start_time = datetime.now()

data = load_yaml_file(Constants.yaml_file)

for file_name in data[Constants.YamlNames.app][Constants.YamlNames.initTransportSystemFiles]:
    data = data_processing.DataProcessing(file_name)
    data.data_shows()
sys.exit()
