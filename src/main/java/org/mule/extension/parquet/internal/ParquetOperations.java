package org.mule.extension.parquet.internal;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.JulianFields;
import java.time.LocalDate;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import java.util.Iterator;
import java.util.List;
import org.apache.parquet.hadoop.metadata.FileMetaData;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import java.io.OutputStream;
import java.io.IOException;
import com.google.gson.JsonElement;
import java.util.Base64;
import com.google.gson.JsonObject;
import org.apache.parquet.tools.read.SimpleRecord;
import org.apache.parquet.schema.Type;
import java.util.ArrayList;
import org.apache.parquet.tools.json.JsonRecordFormatter;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.hadoop.api.ReadSupport;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.tools.read.SimpleReadSupport;
import org.apache.commons.io.IOUtils;
import java.io.FileOutputStream;
import java.io.File;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import java.io.InputStream;

public class ParquetOperations
{
    @MediaType(value = "application/json", strict = false)
    @DisplayName("Read Parquet File")
    public String readParquet(@DisplayName("File Payload") final InputStream fileInputStream, @DisplayName("File Path") final String filePath, @DisplayName("CorrelationId") final String correlationId) {
        ParquetReader<SimpleRecord> reader = null;
        final JsonArray array = new JsonArray();
        final JsonParser parser = new JsonParser();
        String item = null;
        final String parquetFilePath = filePath + File.separator + correlationId + ".parquet";
        final File tempFile = new File(parquetFilePath);
        try {
            final OutputStream outStream = new FileOutputStream(tempFile);
            final byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(outStream);
            reader = (ParquetReader<SimpleRecord>)ParquetReader.builder((ReadSupport)new SimpleReadSupport(), new Path(parquetFilePath)).build();
            final ParquetMetadata metadata = ParquetFileReader.readFooter(new Configuration(), new Path(parquetFilePath));
            final FileMetaData fmd = metadata.getFileMetaData();
            final JsonRecordFormatter.JsonGroupFormatter formatter = JsonRecordFormatter.fromSchema(fmd.getSchema());
            
//            System.out.println("metadata");
//            System.out.println(metadata);

            final List<String> int96Columns = new ArrayList<String>();
            for (int i = 0; i < metadata.getFileMetaData().getSchema().getFields().size(); ++i) {
                final String typeName = metadata.getFileMetaData().getSchema().getFields().get(i).getName();
                final String className = metadata.getFileMetaData().getSchema().getFields().get(i).toString();
                if (className.endsWith("int96 " + typeName)) {
                    int96Columns.add(typeName);
                }
            }
//            System.out.println("int96Columns");
//            System.out.println(int96Columns);
            
            for (SimpleRecord value = (SimpleRecord)reader.read(); value != null; value = (SimpleRecord)reader.read()) {
                item = formatter.formatRecord(value);
                
                final JsonObject jsonObject = (JsonObject)parser.parse(item);
                for (final String column : int96Columns) {
                    if (jsonObject.get(column) != null && jsonObject.get(column).getAsString().length() > 0) {
//                    	System.out.println("unformated date " + jsonObject.get(column).getAsString());
                        final byte[] decodedBytes = Base64.getDecoder().decode(jsonObject.get(column).getAsString());
                        final String timestamp = this.convertInt96ToTimeStamp(decodedBytes);
                        jsonObject.addProperty(column, timestamp);
                    }
                }
                array.add((JsonElement)jsonObject);
            }
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        finally {
            tempFile.delete();
        }
        return array.toString();
    }
    
    private String convertInt96ToTimeStamp(final byte[] int96Bytes) {
//    	System.out.println("input " + int96Bytes);
        int julianDay;
        int index;
        for (julianDay = 0, index = int96Bytes.length; index > 8; --index, julianDay <<= 8, julianDay += (int96Bytes[index] & 0xFF)) {}
        long nanos;
        for (nanos = 0L; index > 0; --index, nanos <<= 8, nanos += (int96Bytes[index] & 0xFF)) {}
        final LocalDateTime timestamp = LocalDate.MIN.with(JulianFields.JULIAN_DAY, (long)julianDay).atTime(LocalTime.MIN).plusNanos(nanos);
//        System.out.println("output " + timestamp.toString());
        return timestamp.toString();
    }
}