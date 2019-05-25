package com.example.myapplication

class InverseMatrix{
    fun findInverse(mat: Array<DoubleArray>): Array<DoubleArray> {
        // GET AN INVERSE MATRIX
        val det: Double = mat[0][0] * ((mat[1][1] * mat[2][2]) - (mat[2][1] * mat[1][2])) - mat[0][1] * (mat[1][0] * mat[2][2] - mat[2][0] * mat[1][2]) + mat[0][2] * (mat[1][0] * mat[2][1] - mat[2][0] * mat[1][1])
        val n = 3
        if (det == 0.0)
            return Array(n) { DoubleArray(n) }
        val matT = Array(n) { DoubleArray(n) }
        val newMatrix = Array(n) { DoubleArray(n) }
        for (i in 0 until n)
            for (j in 0 until n)
                matT[i][j]=mat[j][i]
        // обратная матрица
        newMatrix[0][0] = matT[1][1]*matT[2][2]-matT[1][2]*matT[2][1]
        newMatrix[0][1] = -matT[1][0]*matT[2][2]+matT[1][2]*matT[2][0]
        newMatrix[0][2] = matT[1][0]*matT[2][1]-matT[1][1]*matT[2][0]
        newMatrix[1][0] = -matT[0][1]*matT[2][2] + matT[0][2]*matT[2][1]
        newMatrix[1][1] = matT[0][0]*matT[2][2] - matT[0][2]*matT[2][0]
        newMatrix[1][2] = -matT[0][0]*matT[2][1]+matT[0][1]*matT[2][0]
        newMatrix[2][0] = matT[0][1]*matT[1][2] - matT[1][1]*matT[0][2]
        newMatrix[2][1] = -matT[0][0] * matT[1][2] + matT[0][2]*matT[1][0]
        newMatrix[2][2] = matT[0][0]*matT[1][1] - matT[0][1]*matT[1][0]
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                newMatrix[i][j]/=det
            }
        }
        return newMatrix
    }
}