
package org.mule.extension.parquet.internal;

public final class ParquetConnection
{
    private final String id;
    
    public ParquetConnection(final String id) {
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void invalidate() {
    }
}
