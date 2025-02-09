/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.exchange;

import com.google.inject.Injector;
import io.airlift.bootstrap.Bootstrap;
import io.prestosql.exchange.storage.FileSystemExchangeStorage;
import io.prestosql.plugin.base.jmx.MBeanServerModule;
import io.prestosql.server.PrefixObjectNameGeneratorModule;
import io.prestosql.spi.filesystem.HetuFileSystemClient;
import org.weakref.jmx.guice.MBeanModule;

import java.util.Map;

import static java.util.Objects.requireNonNull;

public class FileSystemExchangeManagerFactory
        implements ExchangeManagerFactory
{
    @Override
    public String getName()
    {
        return "filesystem";
    }

    @Override
    public ExchangeManager create(Map<String, String> config, HetuFileSystemClient fileSystemClient)
    {
        requireNonNull(config, "config is null");
        Bootstrap app = new Bootstrap(
                new MBeanModule(),
                new MBeanServerModule(),
                new PrefixObjectNameGeneratorModule("io.hetu.core.plugin.exchange.filesystem"),
                new FileSystemExchangeModule());
        Injector injector = app.doNotInitializeLogging()
                .setRequiredConfigurationProperties(config)
                .initialize();
        FileSystemExchangeStorage fileSystemExchangeStorage = injector.getInstance(FileSystemExchangeStorage.class);
        fileSystemExchangeStorage.setFileSystemClient(fileSystemClient);
        return injector.getInstance(FileSystemExchangeManager.class);
    }

    @Override
    public ExchangeManagerHandleResolver getHandleResolver()
    {
        return new ExchangeManagerHandleResolver()
        {
            @Override
            public Class<? extends ExchangeSinkInstanceHandle> getExchangeSinkInstanceHandleClass()
            {
                return FileSystemExchangeSinkInstanceHandle.class;
            }

            @Override
            public Class<? extends ExchangeSourceHandle> getExchangeSourceHandleClass()
            {
                return FileSystemExchangeSourceHandle.class;
            }
        };
    }
}
