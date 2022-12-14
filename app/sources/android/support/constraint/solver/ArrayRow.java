package android.support.constraint.solver;

import android.support.constraint.solver.SolverVariable;

public class ArrayRow {
    private static final boolean DEBUG = false;
    static final boolean USE_LINKED_VARIABLES = true;
    float constantValue = 0.0f;
    final float epsilon = 0.001f;
    boolean isSimpleDefinition = false;
    boolean used = false;
    SolverVariable variable = null;
    float variableValue = 0.0f;
    final ArrayLinkedVariables variables;

    public ArrayRow(Cache cache) {
        this.variables = new ArrayLinkedVariables(this, cache);
    }

    public void updateClientEquations() {
        this.variables.updateClientEquations(this);
    }

    public boolean hasAtLeastOnePositiveVariable() {
        return this.variables.hasAtLeastOnePositiveVariable();
    }

    public boolean hasKeyVariable() {
        if (this.variable == null || (this.variable.mType != SolverVariable.Type.UNRESTRICTED && this.constantValue < 0.0f)) {
            return false;
        }
        return USE_LINKED_VARIABLES;
    }

    public String toString() {
        return toReadableString();
    }

    public String toReadableString() {
        String s;
        String s2;
        if (this.variable == null) {
            s = "" + "0";
        } else {
            s = "" + this.variable;
        }
        String s3 = s + " = ";
        boolean addedVariable = false;
        if (this.constantValue != 0.0f) {
            s3 = s3 + this.constantValue;
            addedVariable = USE_LINKED_VARIABLES;
        }
        int count = this.variables.currentSize;
        for (int i = 0; i < count; i++) {
            SolverVariable v = this.variables.getVariable(i);
            if (v != null) {
                float amount = this.variables.getVariableValue(i);
                String name = v.toString();
                if (!addedVariable) {
                    if (amount < 0.0f) {
                        s2 = s2 + "- ";
                        amount *= -1.0f;
                    }
                } else if (amount > 0.0f) {
                    s2 = s2 + " + ";
                } else {
                    s2 = s2 + " - ";
                    amount *= -1.0f;
                }
                if (amount == 1.0f) {
                    s2 = s2 + name;
                } else {
                    s2 = s2 + amount + " " + name;
                }
                addedVariable = USE_LINKED_VARIABLES;
            }
        }
        if (!addedVariable) {
            return s2 + "0.0";
        }
        return s2;
    }

    public void reset() {
        this.variable = null;
        this.variables.clear();
        this.variableValue = 0.0f;
        this.constantValue = 0.0f;
        this.isSimpleDefinition = false;
    }

    public boolean hasVariable(SolverVariable v) {
        return this.variables.containsKey(v);
    }

    public ArrayRow createRowEquals(SolverVariable variable2, int value) {
        if (value < 0) {
            this.constantValue = (float) (value * -1);
            this.variables.put(variable2, 1.0f);
        } else {
            this.constantValue = (float) value;
            this.variables.put(variable2, -1.0f);
        }
        return this;
    }

    public ArrayRow createRowEquals(SolverVariable variableA, SolverVariable variableB, int margin) {
        boolean inverse = false;
        if (margin != 0) {
            int m = margin;
            if (m < 0) {
                m *= -1;
                inverse = USE_LINKED_VARIABLES;
            }
            this.constantValue = (float) m;
        }
        if (!inverse) {
            this.variables.put(variableA, -1.0f);
            this.variables.put(variableB, 1.0f);
        } else {
            this.variables.put(variableA, 1.0f);
            this.variables.put(variableB, -1.0f);
        }
        return this;
    }

    public ArrayRow addSingleError(SolverVariable error, int sign) {
        this.variables.put(error, (float) sign);
        return this;
    }

    public ArrayRow createRowGreaterThan(SolverVariable variableA, SolverVariable variableB, SolverVariable slack, int margin) {
        boolean inverse = false;
        if (margin != 0) {
            int m = margin;
            if (m < 0) {
                m *= -1;
                inverse = USE_LINKED_VARIABLES;
            }
            this.constantValue = (float) m;
        }
        if (!inverse) {
            this.variables.put(variableA, -1.0f);
            this.variables.put(variableB, 1.0f);
            this.variables.put(slack, 1.0f);
        } else {
            this.variables.put(variableA, 1.0f);
            this.variables.put(variableB, -1.0f);
            this.variables.put(slack, -1.0f);
        }
        return this;
    }

    public ArrayRow createRowLowerThan(SolverVariable variableA, SolverVariable variableB, SolverVariable slack, int margin) {
        boolean inverse = false;
        if (margin != 0) {
            int m = margin;
            if (m < 0) {
                m *= -1;
                inverse = USE_LINKED_VARIABLES;
            }
            this.constantValue = (float) m;
        }
        if (!inverse) {
            this.variables.put(variableA, -1.0f);
            this.variables.put(variableB, 1.0f);
            this.variables.put(slack, -1.0f);
        } else {
            this.variables.put(variableA, 1.0f);
            this.variables.put(variableB, -1.0f);
            this.variables.put(slack, 1.0f);
        }
        return this;
    }

    public ArrayRow createRowCentering(SolverVariable variableA, SolverVariable variableB, int marginA, float bias, SolverVariable variableC, SolverVariable variableD, int marginB, boolean withError) {
        if (variableB == variableC) {
            this.variables.put(variableA, 1.0f);
            this.variables.put(variableD, 1.0f);
            this.variables.put(variableB, -2.0f);
        } else if (bias == 0.5f) {
            this.variables.put(variableA, 1.0f);
            this.variables.put(variableB, -1.0f);
            this.variables.put(variableC, -1.0f);
            this.variables.put(variableD, 1.0f);
            if (marginA > 0 || marginB > 0) {
                this.constantValue = (float) ((-marginA) + marginB);
            }
        } else {
            this.variables.put(variableA, (1.0f - bias) * 1.0f);
            this.variables.put(variableB, (1.0f - bias) * -1.0f);
            this.variables.put(variableC, -1.0f * bias);
            this.variables.put(variableD, 1.0f * bias);
            if (marginA > 0 || marginB > 0) {
                this.constantValue = (((float) (-marginA)) * (1.0f - bias)) + (((float) marginB) * bias);
            }
        }
        return this;
    }

    public ArrayRow addError(SolverVariable error1, SolverVariable error2) {
        this.variables.put(error1, 1.0f);
        this.variables.put(error2, -1.0f);
        return this;
    }

    public ArrayRow createRowDimensionPercent(SolverVariable variableA, SolverVariable variableB, SolverVariable variableC, float percent) {
        this.variables.put(variableA, -1.0f);
        this.variables.put(variableB, 1.0f - percent);
        this.variables.put(variableC, percent);
        return this;
    }

    public ArrayRow createRowDimensionRatio(SolverVariable variableA, SolverVariable variableB, SolverVariable variableC, SolverVariable variableD, float ratio) {
        this.variables.put(variableA, -1.0f);
        this.variables.put(variableB, 1.0f);
        this.variables.put(variableC, ratio);
        this.variables.put(variableD, -ratio);
        return this;
    }

    public int sizeInBytes() {
        int size = 0;
        if (this.variable != null) {
            size = 0 + 4;
        }
        return size + 4 + 4 + 4 + this.variables.sizeInBytes();
    }

    public boolean updateRowWithEquation(ArrayRow definition) {
        this.variables.updateFromRow(this, definition);
        return USE_LINKED_VARIABLES;
    }

    public void ensurePositiveConstant() {
        if (this.constantValue < 0.0f) {
            this.constantValue *= -1.0f;
            this.variables.invert();
        }
    }

    public void pickRowVariable() {
        SolverVariable pivotCandidate = this.variables.pickPivotCandidate();
        if (pivotCandidate != null) {
            pivot(pivotCandidate);
        }
        if (this.variables.currentSize == 0) {
            this.isSimpleDefinition = USE_LINKED_VARIABLES;
        }
    }

    public void pivot(SolverVariable v) {
        if (this.variable != null) {
            this.variables.put(this.variable, -1.0f);
            this.variable = null;
        }
        float amount = this.variables.remove(v) * -1.0f;
        this.variable = v;
        this.variableValue = 1.0f;
        if (amount != 1.0f) {
            this.constantValue /= amount;
            this.variables.divideByAmount(amount);
        }
    }
}
