import yaml


def load_yaml_file(file_path):
    """
    Load a YAML file and return the parsed data.

    :param file_path: Path to the YAML file.
    :return: Parsed data from the YAML file.
    """
    with open(file_path, "r") as file:
        return yaml.safe_load(file)