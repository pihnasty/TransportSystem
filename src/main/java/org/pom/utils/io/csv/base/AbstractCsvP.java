package org.pom.utils.io.csv.base;

import com.opencsv.CSVWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class AbstractCsvP implements ReaderP , WriterP {
    private String  path;
    private String fileName;
    private String columnsFormat = "%8.3f  ";
    private char delimiter;
    private List<String> headerList = new ArrayList<>();
    private Locale localeCurrent;
    private boolean isExistFile;

    public AbstractCsvP(String columnsFormat, char delimiter, String  path, String fileName) {
        Path p1 = Paths.get(path);
        if (Files.notExists(p1)) {
            try {
                Files.createDirectories(p1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.isExistFile=!Files.notExists(Paths.get(path+"//"+fileName));

        this.columnsFormat = columnsFormat;
        this.path = path;
        this.fileName = fileName;
        this.delimiter = delimiter;
        this.localeCurrent = Locale.getDefault();
    }

    @Override
    public String getFullPathToFile() {
        return path+"/"+fileName;
    }

    public String getColumnsFormat() {
        return columnsFormat;
    }

    public void setColumnsFormat(String columnsFormat) {
        this.columnsFormat = columnsFormat;
    }

    public int getSizeList(List<Double>... arg) {
        return arg.length!=0? arg[0].size() : 0;
    }

    public File getFile() {
        return  new File(getFullPathToFile());
    }

    public char getDelimiter() {
        return delimiter;
    }

    public void setHeader (String ...arg) {
        headerList.addAll(Arrays.asList(arg));
    }

    public List<String> getHeaderList() {
        return headerList;
    }

    public Locale getLocaleCurrent() {
        return localeCurrent;
    }

    public void writeToFile(List<List<String>> table)  {
        BufferedWriter writer = null;

        try {
            writer = Files.newBufferedWriter(Paths.get( getFullPathToFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (CSVWriter csvWriter = new CSVWriter(writer, getDelimiter(), CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.RFC4180_LINE_END)) {
            List<String[]> data = toStringArray(table);
            csvWriter.writeAll(data);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String[]> toStringArray(List<List<String>> table) {
        List<String[]> records = new ArrayList<>();

        table.forEach( row -> records.add(row.stream().toArray(String[]::new)));
        return records;
    }

    public boolean isExistFile() {
        return isExistFile;
    }
}
