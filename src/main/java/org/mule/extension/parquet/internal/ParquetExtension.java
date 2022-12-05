package org.mule.extension.parquet.internal;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;

@Xml(prefix = "parquet")
@Extension(name = "Parquet-")
@Configurations({ ParquetConfiguration.class })
public class ParquetExtension
{
}