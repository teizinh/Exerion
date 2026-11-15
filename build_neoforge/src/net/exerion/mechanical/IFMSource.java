/*
 * Decompiled with CFR 0.152.
 */
package net.exerion.mechanical;

public interface IFMSource {
    public int getOutputFM();

    default public boolean isGeneratingFM() {
        return this.getOutputFM() > 0;
    }
}

