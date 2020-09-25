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
package uk.ac.ed.ph.jqtiplus.node.item.template.processing;

import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.VariableType;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.Value;

/**
 * @author Jonathon Hare
 */
public final class SetDefaultValue extends ProcessTemplateValue {

    private static final long serialVersionUID = -1151254253813354211L;

    /** Name of this class in xml schema. */
    public static final String QTI_CLASS_NAME = "setDefaultValue";

    public SetDefaultValue(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);
    }

    @Override
    protected void validateThis(final ValidationContext context) {
        super.validateThis(context);
        final Identifier identifier = getIdentifier();
        if (identifier != null) {
            final VariableDeclaration declaration = context.checkLocalVariableReference(this, identifier);
            context.checkVariableType(this, declaration, VariableType.RESPONSE, VariableType.TEMPLATE);
        }
    }

    @Override
    public void evaluate(final ItemProcessingContext context) {
        final Value value = getExpression().evaluate(context);
        if (isThisRuleValid(context)) {
            final Identifier identifier = getIdentifier();
            final VariableDeclaration variableDeclaration = context.ensureVariableDeclaration(identifier, VariableType.RESPONSE, VariableType.TEMPLATE);
            final ItemSessionState itemSessionState = context.getItemSessionState();
            switch (variableDeclaration.getVariableType()) {
                case RESPONSE:
                    itemSessionState.setOverriddenResponseDefaultValue(identifier, value);
                    break;

                case TEMPLATE:
                    itemSessionState.setOverriddenTemplateDefaultValue(identifier, value);
                    break;

                default:
                    throw new QtiLogicException("Unexpected switch case " + variableDeclaration.getVariableType());
            }
        }
        else {
            context.fireRuntimeWarning(this, "Rule is not valid, so discarding computed value " + value.toQtiString());
        }

    }

}
