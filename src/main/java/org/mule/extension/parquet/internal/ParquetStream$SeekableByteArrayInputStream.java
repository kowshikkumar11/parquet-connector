package org.mule.extension.parquet.internal;

import java.io.ByteArrayInputStream;

class ParquetStream$SeekableByteArrayInputStream
  extends ByteArrayInputStream
{
  public ParquetStream$SeekableByteArrayInputStream(byte[] buf)
  {
    super(buf);
  }
  
  public void setPos(int pos)
  {
    this.pos = pos;
  }
  
  public int getPos()
  {
    return pos;
  }
}