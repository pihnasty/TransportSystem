package org.pom.utils.io.csv.write;


import org.pom.utils.io.csv.base.AbstractCsvP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * http://zetcode.com/articles/opencsv/
 */
public class CsvWriterP extends AbstractCsvP {

    public CsvWriterP(String stringFormat, char delimiter, String  path, String fileName) {
        super(stringFormat,delimiter,path,fileName);
    }

    private static List<String[]> toStringArray(List<List<String>> table) {
        List<String[]> records = new ArrayList<>();
        table.forEach( row -> records.add(row.toArray(String[]::new)));
        return records;
    }

    @Override
    public List<List<String>> readFromFile() throws IOException {
        return null;
    }

}

