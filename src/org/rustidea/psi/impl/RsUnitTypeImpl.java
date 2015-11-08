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

package org.rustidea.psi.impl;

import org.jetbrains.annotations.NotNull;
import org.rustidea.psi.IRsType;
import org.rustidea.psi.RsElementVisitor;
import org.rustidea.psi.RsUnitType;
import org.rustidea.psi.types.RsPsiTypes;
import org.rustidea.util.SimpleArrayFactory;

public class RsUnitTypeImpl extends IRsCompositePsiElement implements RsUnitType {
    public RsUnitTypeImpl() {
        super(RsPsiTypes.UNIT_TYPE);
    }

    @NotNull
    @Override
    public IRsType[] getTypes() {
        return SimpleArrayFactory.empty(IRsType.class);
    }

    @Override
    public int getTypeIndex(@NotNull IRsType type) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("unit type does not have children");
    }

    @Override
    public void accept(@NotNull RsElementVisitor visitor) {
        visitor.visitUnitType(this);
    }
}