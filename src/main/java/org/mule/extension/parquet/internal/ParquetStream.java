package org.mule.extension.parquet.internal;

import java.io.IOException;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.SeekableInputStream;

public class ParquetStream
  implements InputFile
{
  private final String streamId;
  private final byte[] data;
  
  public ParquetStream(String streamId, byte[] data)
  {
    this.streamId = streamId;
    this.data = data;
  }
  
  public long getLength()
    throws IOException
  {
    return data.length;
  }
  
  public SeekableInputStream newStream()
    throws IOException
  {
    return new ParquetStream$1(this, new ParquetStream$SeekableByteArrayInputStream(data));
  }
  
  public String toString()
  {
    return "ParquetStream[" + streamId + "]";
  }
}