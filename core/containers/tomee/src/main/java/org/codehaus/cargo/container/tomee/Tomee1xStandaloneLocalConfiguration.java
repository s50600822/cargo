/*
 * ========================================================================
 *
 * Codehaus CARGO, copyright 2004-2011 Vincent Massol, 2012-2017 Ali Tokmen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ========================================================================
 */
package org.codehaus.cargo.container.tomee;

import java.util.HashMap;
import java.util.Map;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.LocalContainer;
import org.codehaus.cargo.container.configuration.ConfigurationCapability;
import org.codehaus.cargo.container.tomcat.Tomcat7xStandaloneLocalConfiguration;
import org.codehaus.cargo.container.tomcat.TomcatCopyingInstalledLocalDeployer;
import org.codehaus.cargo.container.tomee.internal.TomeeStandaloneLocalConfigurationCapability;

/**
 * Standalone local configuration for TomEE 1.x.
 */
public class Tomee1xStandaloneLocalConfiguration extends Tomcat7xStandaloneLocalConfiguration
{

    /**
     * {@inheritDoc}
     */
    private static ConfigurationCapability capability =
        new TomeeStandaloneLocalConfigurationCapability();

    /**
     * {@inheritDoc}
     * @see Tomcat7xStandaloneLocalConfiguration#Tomcat7xStandaloneLocalConfiguration(String)
     */
    public Tomee1xStandaloneLocalConfiguration(String dir)
    {
        super(dir);

        setProperty(TomeePropertySet.APPS_DIRECTORY, "apps");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfigurationCapability getCapability()
    {
        return capability;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "TomEE 1.x Standalone Configuration";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doConfigure(LocalContainer container) throws Exception
    {
        super.doConfigure(container);

        String apps = getPropertyValue(TomeePropertySet.APPS_DIRECTORY);
        String tomeeXml = getFileHandler().append(getHome(), "conf/tomee.xml");
        Map<String, String> replacements = new HashMap<String, String>(1);
        replacements.put(
            "<!-- activate next line to be able to deploy applications in apps -->",
            "<!-- activate deployment applications in " +  apps + " directory -->");
        replacements.put(
            "<!-- <Deployments dir=\"apps\" /> -->",
            "<Deployments dir=\"" +  apps + "\" />");
        getFileHandler().replaceInFile(tomeeXml, replacements, "UTF-8");
    }

    /**
     * {@inheritDoc} TomEE provides its own transaction factory with openejb, so we don't add
     * <code>org.objectweb.jotm.UserTransactionFactory</code> unlike Tomcat
     */
    @Override
    protected void setupTransactionManager()
    {
        // Nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TomcatCopyingInstalledLocalDeployer createDeployer(LocalContainer container)
    {
        return new TomeeCopyingInstalledLocalDeployer((InstalledLocalContainer) container);
    }

}
