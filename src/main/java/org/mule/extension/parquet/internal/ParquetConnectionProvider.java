
package org.mule.extension.parquet.internal;

import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.ConnectionException;
import org.slf4j.LoggerFactory;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.slf4j.Logger;
import org.mule.runtime.api.connection.PoolingConnectionProvider;

public class ParquetConnectionProvider implements PoolingConnectionProvider<ParquetConnection>
{
    private final Logger LOGGER;
    @Parameter
    private String requiredParameter;
    @DisplayName("Friendly Name")
    @Parameter
    @Optional(defaultValue = "100")
    private int optionalParameter;
    
    public ParquetConnectionProvider() {
        this.LOGGER = LoggerFactory.getLogger((Class)ParquetConnectionProvider.class);
    }
    
    public ParquetConnection connect() throws ConnectionException {
        return new ParquetConnection(this.requiredParameter + ":" + this.optionalParameter);
    }
    
    public void disconnect(final ParquetConnection connection) {
        try {
            connection.invalidate();
        }
        catch (Exception e) {
            this.LOGGER.error("Error while disconnecting [" + connection.getId() + "]: " + e.getMessage(), (Throwable)e);
        }
    }
    
    public ConnectionValidationResult validate(final ParquetConnection connection) {
        return ConnectionValidationResult.success();
    }
}