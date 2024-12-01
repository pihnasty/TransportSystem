package org.pom.utils.io.csv.read;


import com.opencsv.CSVReader;
import org.pom.utils.io.csv.base.AbstractCsvP;


import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvReaderP extends AbstractCsvP {

    public CsvReaderP(String columnsFormat, char delimiter, String path, String fileName) {
        super(columnsFormat, delimiter, path, fileName);
    }

    @Override
    public  List<List<String>> readFromFile() throws IOException {
        List<List<String>> rows = new ArrayList<>();
        if (!isExistFile()) return rows;
        Reader reader = new FileReader(getFile());
        try (CSVReader csvReader = new CSVReader(reader, getDelimiter())) {
            String[] record = null;
            while ((record = csvReader.readNext()) != null) {
                List<String> row = new ArrayList<>();
                row.addAll(Arrays.asList(record));
                rows.add(row);
            }
        }
        return rows;
    }


}
