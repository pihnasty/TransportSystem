package org.pom.utils.io.csv.base;

import java.io.IOException;
import java.util.List;

public interface ReaderP {
    List<List<String>> readFromFile() throws IOException;
    String getFullPathToFile();
}
