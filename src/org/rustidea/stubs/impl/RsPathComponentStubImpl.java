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
import com.intellij.util.io.StringRef;
import org.rustidea.psi.RsPathComponent;
import org.rustidea.psi.types.RsTypes;
import org.rustidea.stubs.RsPathComponentStub;

public class RsPathComponentStubImpl extends NamedStubBase<RsPathComponent> implements RsPathComponentStub {
    public RsPathComponentStubImpl(final StubElement parent, final StringRef name) {
        super(parent, RsTypes.PATH_COMPONENT, name);
    }
}
