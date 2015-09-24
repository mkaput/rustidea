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

package org.rustidea.stubs.impl;

import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.util.QualifiedName;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NotNull;
import org.rustidea.psi.RsUseDecl;
import org.rustidea.psi.types.RsTypes;
import org.rustidea.stubs.RsUseDeclStub;

public class RsUseDeclStubImpl extends NamedStubBase<RsUseDecl> implements RsUseDeclStub {
    @NotNull
    private final QualifiedName[] resolvedNames;

    public RsUseDeclStubImpl(StubElement parent, StringRef name, @NotNull QualifiedName[] resolvedNames) {
        super(parent, RsTypes.USE_DECL, name);
        this.resolvedNames = resolvedNames;
    }

    @NotNull
    @Override
    public QualifiedName[] getResolvedNames() {
        return resolvedNames;
    }
}