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
package io.prestosql.parquet.writer;

import io.airlift.slice.Slice;
import io.airlift.slice.SliceOutput;
import org.apache.parquet.bytes.BytesInput;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public interface ParquetDataOutput
{
    static ParquetDataOutput createDataOutput(Slice slice)
    {
        requireNonNull(slice, "slice is null");
        return new ParquetDataOutput()
        {
            @Override
            public long size()
            {
                return slice.length();
            }

            @Override
            public void writeData(SliceOutput sliceOutput)
            {
                sliceOutput.writeBytes(slice);
            }
        };
    }

    static ParquetDataOutput createDataOutput(BytesInput bytesInput)
    {
        requireNonNull(bytesInput, "bytesInput is null");
        return new ParquetDataOutput()
        {
            @Override
            public long size()
            {
                return bytesInput.size();
            }

            @Override
            public void writeData(SliceOutput sliceOutput)
            {
                try {
                    bytesInput.writeAllTo(sliceOutput);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    /**
     * Number of bytes that will be written.
     */
    long size();

    /**
     * Writes data to the output. The output must be exactly
     * {@link #size()} bytes.
     */
    void writeData(SliceOutput sliceOutput);
}
