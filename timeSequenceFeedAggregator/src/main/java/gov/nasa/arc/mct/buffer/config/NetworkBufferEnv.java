/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package gov.nasa.arc.mct.buffer.config;

import gov.nasa.arc.mct.api.feed.DataProvider.LOS;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkBufferEnv implements DataBufferEnv, Cloneable {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkBufferEnv.class);
    
    private static Properties loadDefaultPropertyFile() {
        Properties prop = new Properties();
        InputStream is = null;
        try {
             is = ClassLoader.getSystemResourceAsStream("properties/feed.properties");
            prop.load(is);
        } catch (Exception e) {
            LOGGER.error("Cannot initialized DataBufferEnv properties", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                    // ignore exception
                }
            }
        }
        return prop;
    }
    
    
    private final Properties prop;
    private final int numOfBufferPartitions;
    private int currentBufferPartition;
    private final String networkBufferServerHost;
    private final int networkBufferServerPort;
    
    public NetworkBufferEnv(Properties prop) {
        if (prop == null) {
            prop = loadDefaultPropertyFile();
        }
        this.prop = prop;
        numOfBufferPartitions = Integer.parseInt(prop.getProperty("network.buffer.partition"));
        networkBufferServerHost = prop.getProperty("network.buffer.server.host");
        networkBufferServerPort = Integer.parseInt(prop.getProperty("network.buffer.server.port"));
        this.currentBufferPartition = 0;
    }
    
    public NetworkBufferEnv(Properties prop, int currentBufferPartition) {
        if (prop == null) {
            prop = loadDefaultPropertyFile();
        }
        this.prop = prop;
        this.currentBufferPartition = currentBufferPartition;
        numOfBufferPartitions = Integer.parseInt(prop.getProperty("network.buffer.partition"));
        networkBufferServerHost = prop.getProperty("network.buffer.server.host");
        networkBufferServerPort = Integer.parseInt(prop.getProperty("network.buffer.server.port"));
    }

    @Override
    public final long getBufferPartitionOverlap() {
        return 0;
    }

    @Override
    public long getBufferTime() {
        return -1;
    }

    @Override
    public int getCurrentBufferPartition() {
        return currentBufferPartition;
    }

    @Override
    public int getNumOfBufferPartitions() {
        return this.numOfBufferPartitions;
    }
    
    @Override
    public int nextBufferPartition() {
        return (this.currentBufferPartition+1)%numOfBufferPartitions;
    }
    
    @Override
    public int previousBufferPartition(int currentPartition) {
        int i = currentPartition;
        if (i == 0) {
            i = this.numOfBufferPartitions-1;
        } else {
            i--;
        }
        return i;
    }
    
    @Override
    public DataBufferEnv advanceBufferPartition() {
        int nextBufferPartition = nextBufferPartition();
        NetworkBufferEnv newBufferEnv = new NetworkBufferEnv(prop, nextBufferPartition);
        return newBufferEnv;
    }
    
    @Override
    public void closeAndRestartEnvironment() {
        this.currentBufferPartition = 0;
    }
    
    @Override
    public int getBufferWriteThreadPoolSize() {
        return 1;
    }
    
    @Override
    public int getConcurrencyDegree() {
        return 1;
    }
    
    @Override
    public void restartEnvironment(boolean isReadOnly) {
        this.currentBufferPartition = 0;
    }
    
    @Override
    public Object clone() {
        return new NetworkBufferEnv(prop);
    }
    
    @Override
    public Object cloneMetaBuffer() {
        return new NetworkBufferEnv(prop);
    }

    public String getNetworkBufferServerHost() {
        return networkBufferServerHost;
    }

    public int getNetworkBufferServerPort() {
        return networkBufferServerPort;
    }
    
    @Override
    public Properties getConfigProperties() {
        return this.prop;
    }

    @Override
    public LOS getLOS() {
        return LOS.medium;
    }
    
    @Override
    public void flush() {
        // TODO Auto-generated method stub
        
    }
}
