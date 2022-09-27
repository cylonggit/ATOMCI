package com.chen.crossTest.pojo.proof;

import lombok.Data;

@Data
public class OperationProof {
    int Operation;
    String chainId;
    SubInvocation read;
    SubInvocation lock_do;
    SubInvocation unlock;
    SubInvocation undo_unlock;
}
