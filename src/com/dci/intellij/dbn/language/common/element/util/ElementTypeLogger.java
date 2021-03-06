/*
 * Copyright 2012-2014 Dan Cioca
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dci.intellij.dbn.language.common.element.util;

import com.dci.intellij.dbn.common.options.setting.SettingsUtil;
import com.dci.intellij.dbn.language.common.element.ElementType;
import com.dci.intellij.dbn.language.common.element.IdentifierElementType;
import com.dci.intellij.dbn.language.common.element.IterationElementType;
import com.dci.intellij.dbn.language.common.element.NamedElementType;
import com.dci.intellij.dbn.language.common.element.OneOfElementType;
import com.dci.intellij.dbn.language.common.element.QualifiedIdentifierElementType;
import com.dci.intellij.dbn.language.common.element.SequenceElementType;
import com.dci.intellij.dbn.language.common.element.TokenElementType;
import com.dci.intellij.dbn.language.common.element.parser.ParseResultType;
import com.intellij.lang.PsiBuilder;

public class ElementTypeLogger {
    private ElementType elementType;

    public ElementTypeLogger(ElementType elementType) {
        this.elementType = elementType;
    }

    public void logBegin(PsiBuilder builder, boolean optional, int depth) {
        // GTK enable disable debug
        if (SettingsUtil.isDebugEnabled) {
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < depth; i++) buffer.append('\t');
            buffer.append('"').append(elementType.getId()).append("\" [");
            buffer.append(getTypeDescription());
            buffer.append(": ");
            buffer.append(optional ? "optional" : "mandatory");
            buffer.append("] '").append(builder.getTokenText()).append("'");
            if (elementType.isLeaf()) System.out.print(buffer.toString());
            else System.out.println(buffer.toString());
            //log.info(msg);
        }
    }

    public void logEnd(ParseResultType resultType, int depth) {
        if (SettingsUtil.isDebugEnabled) {
            StringBuilder buffer = new StringBuilder();
            if (!elementType.isLeaf()) {
                for (int i = 0; i < depth; i++) buffer.append('\t');
                buffer.append('"').append(elementType.getId()).append('"');
            }
            buffer.append(" >> ");
            switch (resultType) {
                case FULL_MATCH: buffer.append("Matched"); break;
                case PARTIAL_MATCH: buffer.append("Partially matched"); break;
                case NO_MATCH: buffer.append("Not matched"); break;
            }
            System.out.println(buffer.toString());
            //log.info(msg);
        }
    }

    private String getTypeDescription(){
        if (elementType instanceof TokenElementType) return "token";
        if (elementType instanceof NamedElementType) return "element";
        if (elementType instanceof SequenceElementType) return "sequence";
        if (elementType instanceof IterationElementType) return "iteration";
        if (elementType instanceof QualifiedIdentifierElementType) return "qualified-identifier";
        if (elementType instanceof OneOfElementType) return "one-of";
        if (elementType instanceof IdentifierElementType) {
            IdentifierElementType iet = (IdentifierElementType) elementType;
            return  iet.getDebugName();
        }
        return null;
    }

    public void logErr(PsiBuilder builder, boolean optional, int depth) {
        logBegin(builder, optional, depth);
        logEnd(ParseResultType.NO_MATCH, depth);
    }
}
