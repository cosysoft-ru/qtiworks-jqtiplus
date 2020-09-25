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
package uk.ac.ed.ph.jqtiplus.node.test.outcome.processing;

import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.LookupTable;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.running.TestProcessingContext;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * The setOutcomeValue rule sets the value of an outcome variable to the value obtained from the associated expression.
 * An outcome variable can be updated with reference to a previously assigned value, in other words, the outcome variable
 * being set may appear in the expression where it takes the value previously assigned to it.
 * <p>
 * Special care is required when using the numeric base-types because floating point values can not be assigned to integer variables and vice-versa. The
 * truncate, round, or integerToFloat operators must be used to achieve numeric type conversion.
 *
 * @author Jiri Kajaba
 */
public final class SetOutcomeValue extends ProcessOutcomeValue {

    private static final long serialVersionUID = -7790249550437462840L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "setOutcomeValue";

    public SetOutcomeValue(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);
    }

    @Override
    public final Cardinality[] getRequiredCardinalities(final ValidationContext context, final int index) {
        final Identifier referenceIdentifier = getIdentifier();
        if (referenceIdentifier!=null) {
            final VariableDeclaration declaration = context.isValidLocalVariableReference(referenceIdentifier);
            if (declaration!=null) {
                return new Cardinality[] {  declaration.getCardinality() };
            }
        }
        return Cardinality.values();
    }

    @Override
    public final BaseType[] getRequiredBaseTypes(final ValidationContext context, final int index) {
        final Identifier referenceIdentifier = getIdentifier();
        if (referenceIdentifier!=null) {
            final VariableDeclaration declaration = context.isValidLocalVariableReference(referenceIdentifier);
            if (declaration!=null && declaration.getBaseType()!=null) {
                return new BaseType[] { declaration.getBaseType() };
            }
        }
        return BaseType.values();
    }

    @Override
    public void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final Identifier referenceIdentifier = getIdentifier();
        if (referenceIdentifier!=null) {
            final OutcomeDeclaration declaration = (OutcomeDeclaration) context.checkLocalVariableReference(this, referenceIdentifier);
            if (declaration!=null && declaration.getLookupTable() != null) {
                context.fireValidationWarning(this, "Never used " + LookupTable.DISPLAY_NAME
                        + " in "
                        + OutcomeDeclaration.QTI_CLASS_NAME
                        + ": "
                        + referenceIdentifier);
            }
        }
    }

    @Override
    public void evaluate(final TestProcessingContext context) {
        final Value value = getExpression().evaluate(context);
        if (isThisRuleValid(context)) {
            final OutcomeDeclaration outcomeDeclaration = (OutcomeDeclaration) context.ensureVariableDeclaration(getIdentifier(), VariableType.OUTCOME);
            context.getTestSessionState().setOutcomeValue(outcomeDeclaration, value);
        }
        else {
            context.fireRuntimeWarning(this, "Rule is not valid, so discarding computed value " + value.toQtiString());
        }
    }
}
