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

package org.rustidea.stubs.types;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.tree.ICompositeElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.rustidea.RustLanguage;

public abstract class IRsStubElementType<StubT extends StubElement, PsiT extends PsiElement>
    extends IStubElementType<StubT, PsiT>
    implements ICompositeElementType {
    protected IRsStubElementType(@NotNull @NonNls String debugName) {
        super(debugName, RustLanguage.INSTANCE);
    }

    public abstract PsiT createPsi(ASTNode node);

    @Override
    public void indexStub(@NotNull StubT stub, @NotNull IndexSink sink) {
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "rust." + toString();
    }
}
