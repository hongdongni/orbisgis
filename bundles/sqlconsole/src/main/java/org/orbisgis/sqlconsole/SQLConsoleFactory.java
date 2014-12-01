/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 * 
 * This file is part of OrbisGIS.
 * 
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 * 
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.sqlconsole;

import org.fife.rsta.ac.LanguageSupport;
import org.orbisgis.sqlparserapi.ScriptSplitterFactory;
import org.orbisgis.viewapi.edition.EditorDockable;
import org.orbisgis.viewapi.edition.EditorFactory;
import org.orbisgis.viewapi.edition.SingleEditorFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Set;


/**
 * Create a single instance of SQLConsole and
 * manage the declaration of the SQLMetadataManager service.
 * @author Nicolas Fortin
 */
@Component(service = EditorFactory.class, immediate = true)
public class SQLConsoleFactory implements SingleEditorFactory {

        public static final String factoryId = "SQLConsoleFactory";
        protected final static I18n I18N = I18nFactory.getI18n(SQLConsoleFactory.class);
        private SQLConsole sqlConsole;
        private DataSource dataSource;
        private ScriptSplitterFactory scriptSplitterFactory;
        private Set<LanguageSupport> languageSupports = new HashSet<>();


        /**
         * Default constructor
         */
        public SQLConsoleFactory() {

        }


        @Reference
        public void setDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public void unsetDataSource(DataSource dataSource) {
            this.dataSource = null;
        }

        @Reference
        public void setScriptSplitterFactory(ScriptSplitterFactory scriptSplitterFactory) {
            this.scriptSplitterFactory = scriptSplitterFactory;
            if(sqlConsole != null) {
                sqlConsole.setSplitterFactory(scriptSplitterFactory);
            }
        }

        public void unsetScriptSplitterFactory(ScriptSplitterFactory scriptSplitterFactory) {
            setScriptSplitterFactory(null);
        }

        @Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE, policy = ReferencePolicy.DYNAMIC)
        public void addLanguageSupport(LanguageSupport languageSupport) {
            languageSupports.add(languageSupport);
            if(sqlConsole != null) {
                languageSupport.install(sqlConsole.getScriptPanel());
            }
        }

        public void removeLanguageSupport(LanguageSupport languageSupport) {
            languageSupports.remove(languageSupport);
            if(sqlConsole != null) {
                languageSupport.uninstall(sqlConsole.getScriptPanel());
            }
        }

        @Override
        public EditorDockable[] getSinglePanels() {
                if(sqlConsole==null) {
                        sqlConsole = new SQLConsole(dataSource);
                        sqlConsole.setSplitterFactory(scriptSplitterFactory);
                        for(LanguageSupport languageSupport : languageSupports) {
                            languageSupport.install(sqlConsole.getScriptPanel());
                        }
                }
                return new EditorDockable[] {sqlConsole};
        }

        @Override
        public String getId() {
                return factoryId;
        }

        @Override
        public void dispose() {
                if(sqlConsole != null) {
                    for(LanguageSupport languageSupport : languageSupports) {
                        languageSupport.install(sqlConsole.getScriptPanel());
                    }
                    sqlConsole.dispose();
                }
        }
}
