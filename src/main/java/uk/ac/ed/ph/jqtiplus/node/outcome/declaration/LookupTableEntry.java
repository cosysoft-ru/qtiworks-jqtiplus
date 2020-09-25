/* Copyright (c) 2012-2013, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.node.outcome.declaration;

import uk.ac.ed.ph.jqtiplus.attribute.value.SingleValueAttribute;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;

/**
 * Abstract entry for lookupTable.
 *
 * @author Jiri Kajaba
 */
public abstract class LookupTableEntry<N extends Number> extends AbstractNode {

    private static final long serialVersionUID = 3321503230408602996L;

    /** Name of targetValue attribute in xml schema. */
    public static final String ATTR_TARGET_VALUE_NAME = "targetValue";

    public LookupTableEntry(final LookupTable<?,?> parent, final String qtiClassName) {
        super(parent, qtiClassName);

        getAttributes().add(new SingleValueAttribute(this, ATTR_TARGET_VALUE_NAME, parent.getTargetValueBaseType(), true));
    }

    @Override
    public LookupTable<?,?> getParent() {
        return (LookupTable<?,?>) super.getParent();
    }

    /**
     * Gets numeric value of sourceValue attribute.
     */
    public abstract N getSourceValue();

    public abstract void setSourceValue(N sourceValue);

    public SingleValue getTargetValue() {
        return getAttributes().getSingleValueAttribute(ATTR_TARGET_VALUE_NAME).getComputedValue();
    }

    public void setTargetValue(final SingleValue targetValue) {
        getAttributes().getSingleValueAttribute(ATTR_TARGET_VALUE_NAME).setValue(targetValue);
    }


    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        if (getParent().getParent().getBaseType() != null) {
            getAttributes().getSingleValueAttribute(ATTR_TARGET_VALUE_NAME).setBaseType(getParent().getParent().getBaseType());
        }
    }
}
