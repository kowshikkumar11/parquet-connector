package org.mule.extension.parquet.internal;

import java.io.IOException;
import java.io.InputStream;
import org.apache.parquet.io.DelegatingSeekableInputStream;

class ParquetStream$1
  extends DelegatingSeekableInputStream
{
  ParquetStream$1(ParquetStream this$0, InputStream stream)
  {
    super(stream);
  }
  
  public void seek(long newPos)
    throws IOException
  {
    ((ParquetStream$SeekableByteArrayInputStream)getStream()).setPos((int)newPos);
  }
  
  public long getPos()
    throws IOException
  {
    return ((ParquetStream$SeekableByteArrayInputStream)getStream()).getPos();
  }
}