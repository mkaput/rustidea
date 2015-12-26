/*
 * Copyright 2015 Marek Kaput
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
 */

package org.rustidea.parser;

import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

import static com.intellij.lang.PsiBuilderUtil.expect;
import static org.rustidea.parser.RsParserUtil.*;
import static org.rustidea.psi.types.RsPsiTypes.*;

class RsTypeParser extends IRsParserBase {
    private static final Logger LOG = Logger.getInstance(RsTypeParser.class);
    private static final TokenSet CONST_OR_MUT = TokenSet.create(KW_CONST, KW_MUT);

    public RsTypeParser(@NotNull final RsParser parser) {
        super(parser);
    }

    public boolean lifetime() {
        return wrap(builder, PRIM_IDENT, LIFETIME);
    }

    public boolean lifetimeTypeParameter() {
        final Marker marker = builder.mark();
        if (lifetime()) {
            marker.done(LIFETIME_TYPE_PARAMETER);
            return true;
        } else {
            marker.rollbackTo();
            return false;
        }
    }

    public boolean typeParameter() {
        final Marker marker = builder.mark();

        if (!identifier(builder)) {
            marker.rollbackTo();
            return false;
        }

        // TODO:RJP-13 Implement type bounds

        marker.done(TYPE_PARAMETER);
        return true;
    }

    public boolean typeParameterList() {
        return parenthesize(builder, OP_LT, OP_GT, new VoidParserWrapper() {
            @Override
            public void parse() {
                sep(builder, OP_COMMA, new ParserWrapper() {
                    @Override
                    public boolean parse() {
                        if (lifetimeTypeParameter() || typeParameter()) {
                            return true;
                        } else {
                            error(builder, "expected type parameter or lifetime");
                            return false;
                        }
                    }
                }, EnumSet.of(SepCfg.TOLERATE_EMPTY));
            }
        }, TYPE_PARAMETER_LIST);
    }

    public boolean type() {
        boolean result = tupleType();
        result = result || listType();
        result = result || referenceType();
        result = result || rawPointerType();
        result = result || functionType();
        result = result || pathType();
        return result;
    }

    public boolean expectType() {
        final Marker marker = builder.mark();
        if (type()) {
            marker.drop();
            return true;
        } else {
            marker.rollbackTo();
            error(builder, "expected type");
            return false;
        }
    }

    public boolean typeList() {
        return doTypeList(OP_LT, OP_GT, TYPE_LIST);
    }

    private boolean doTypeList(@NotNull final IElementType lparen,
                               @NotNull final IElementType rparen,
                               @NotNull final IElementType elementType) {
        return parenthesize(builder, lparen, rparen, new VoidParserWrapper() {
            @Override
            public void parse() {
                sep(builder, OP_COMMA, new ParserWrapper() {
                    @Override
                    public boolean parse() {
                        return expectType();
                    }
                }, EnumSet.of(SepCfg.TOLERATE_EMPTY));
            }
        }, elementType);
    }

    public boolean pathType() {
        final Marker marker = builder.mark();
        if (parser.getReferenceParser().path()) {
            marker.done(PATH_TYPE);
            return true;
        } else {
            marker.rollbackTo();
            return false;
        }
    }

    public boolean structureType() {
        return parenthesize(builder, OP_LBRACE, OP_RBRACE, new VoidParserWrapper() {
            @Override
            public void parse() {
                sep(builder, OP_COMMA, new ParserWrapper() {
                    @Override
                    public boolean parse() {
                        if (structField()) {
                            return true;
                        } else {
                            errorExpected(builder, STRUCT_FIELD);
                            return false;
                        }
                    }
                }, EnumSet.of(SepCfg.ALLOW_TRAILING, SepCfg.TOLERATE_EMPTY));
            }
        }, STRUCT_TYPE);
    }

    public boolean tupleType() {
        final Marker marker = builder.mark();

        if (!expect(builder, OP_LPAREN)) {
            marker.rollbackTo();
            return false;
        }

        // Check if we have unit type
        if (expect(builder, OP_RPAREN)) {
            marker.done(UNIT_TYPE);
            return true;
        }

        sep(builder, OP_COMMA, new ParserWrapper() {
            @Override
            public boolean parse() {
                return expectType();
            }
        }, EnumSet.of(SepCfg.TOLERATE_EMPTY));

        expectOrWarn(builder, OP_RPAREN);

        marker.done(TUPLE_TYPE);
        return true;
    }

    public boolean listType() {
        final Marker marker = builder.mark();
        boolean isArray = false;

        if (!expect(builder, OP_LBRACKET)) {
            marker.rollbackTo();
            return false;
        }

        expectType();

        if (expect(builder, OP_SEMICOLON)) {
            isArray = true;
            parser.getExpressionParser().expectInteger();
        }

        expectOrWarnMissing(builder, OP_RBRACKET);

        marker.done(isArray ? ARRAY_TYPE : SLICE_TYPE);
        return true;
    }

    public boolean referenceType() {
        final Marker marker = builder.mark();

        if (!expect(builder, OP_AND)) {
            marker.rollbackTo();
            return false;
        }

        lifetime();
        expectType();

        marker.done(REFERENCE_TYPE);
        return true;
    }

    public boolean rawPointerType() {
        final Marker marker = builder.mark();

        if (!expect(builder, OP_ASTERISK)) {
            marker.rollbackTo();
            return false;
        }

        if (!expect(builder, CONST_OR_MUT)) {
            error(builder, "expected 'const' or 'mut'");
        }

        expectType();

        marker.done(RAW_POINTER_TYPE);
        return true;
    }

    public boolean functionType() {
        final Marker marker = builder.mark();

        @SuppressWarnings("unused")
        boolean modifier = parser.getModuleParser().externModifier() || expect(builder, KW_UNSAFE);

        // I.   fn(i32) -> i32
        // II.  FnMut(i32) -> i32
        if (!expect(builder, KW_FN) &&
            !(parser.getReferenceParser().path() && builder.getTokenType() == OP_LPAREN)) {
            marker.rollbackTo();
            return false;
        }

        if (!doTypeList(OP_LPAREN, OP_RPAREN, INPUT_TYPE_LIST)) {
            errorExpected(builder, INPUT_TYPE_LIST);
        }

        if (expect(builder, OP_ARROW)) {
            expectType();
        }

        marker.done(FUNCTION_TYPE);
        return true;
    }

    public boolean structField() {
        final Marker marker = builder.mark();

        if (!identifier(builder)) {
            marker.rollbackTo();
            return false;
        }

        expectOrWarn(builder, OP_COLON);

        expectType();

        marker.done(STRUCT_FIELD);
        return true;
    }
}
