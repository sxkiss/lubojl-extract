.class public Lcom/tianyu/util/Configuration;
.super Ljava/lang/Object;
.source "SourceFile"


# static fields
.field public static ENABLE_CRASH_REPORT:Z

.field public static ENABLE_PT:Z


# direct methods
.method static constructor <clinit>()V
    .locals 1

    const/4 v0, 0x0

    const/4 v0, 0x1

    sput-boolean v0, Lcom/tianyu/util/Configuration;->ENABLE_CRASH_REPORT:Z

    sput-boolean v0, Lcom/tianyu/util/Configuration;->ENABLE_PT:Z

    return-void
.end method

.method public constructor <init>()V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method
