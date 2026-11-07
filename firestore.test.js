const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const CITIZEN_UID = "citizen_alice";
const OTHER_CITIZEN_UID = "citizen_bob";
const OPERATOR_UID = "operator_ramesh";
const ADMIN_UID = "admin_vikram";
const SUSPENDED_OPERATOR_UID = "operator_suspended";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();

    // Bootstrap Server-Side Authoritative Roles
    await testEnv.withSecurityRulesDisabled(async (context) => {
      const db = context.firestore();

      // Super Admin Account
      await db.collection("user_roles").doc(ADMIN_UID).set({
        userId: ADMIN_UID,
        role: "SUPER_ADMIN",
        status: "ACTIVE",
        email: "admin@yojanamitra.gov.in",
        createdAt: new Date(),
        updatedAt: new Date(),
      });

      // Active Operator with Assigned Jurisdiction (Panchayat: Nohsa, District: Patna)
      await db.collection("user_roles").doc(OPERATOR_UID).set({
        userId: OPERATOR_UID,
        role: "OPERATOR",
        status: "ACTIVE",
        email: "operator.nohsa@csc.gov.in",
        jurisdictionState: "Bihar",
        jurisdictionDistrict: "Patna",
        jurisdictionBlock: "Phulwari Sharif",
        jurisdictionPanchayat: "Nohsa",
        createdAt: new Date(),
        updatedAt: new Date(),
      });

      // Suspended Operator
      await db.collection("user_roles").doc(SUSPENDED_OPERATOR_UID).set({
        userId: SUSPENDED_OPERATOR_UID,
        role: "OPERATOR",
        status: "SUSPENDED",
        email: "suspended@csc.gov.in",
        jurisdictionState: "Bihar",
        jurisdictionDistrict: "Patna",
        jurisdictionBlock: "Phulwari Sharif",
        jurisdictionPanchayat: "Nohsa",
        createdAt: new Date(),
        updatedAt: new Date(),
      });

      // Regular Citizen
      await db.collection("user_roles").doc(CITIZEN_UID).set({
        userId: CITIZEN_UID,
        role: "CITIZEN",
        status: "ACTIVE",
        email: "alice@gmail.com",
        createdAt: new Date(),
        updatedAt: new Date(),
      });
    });
  }
});

test("RBAC 1: Unauthenticated request cannot access any records", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("citizen_profiles").doc("p1").get());
  await assertFails(unauthDb.collection("user_roles").doc(ADMIN_UID).get());
  await assertFails(unauthDb.collection("audit_logs").doc("log1").get());
});

test("RBAC 2: Citizen cannot modify or write government schemes (Admin only)", async () => {
  const citizenDb = testEnv.authenticatedContext(CITIZEN_UID).firestore();
  await assertFails(
    citizenDb.collection("schemes").doc("fake_scheme").set({
      schemeName: "Fake Scheme",
      schemeNameHindi: "फर्जी योजना",
      category: "Agriculture",
      department: "Test",
      isPublished: true,
      createdAt: new Date(),
      updatedAt: new Date(),
    })
  );
});

test("RBAC 3: Super Admin CAN create and publish government schemes", async () => {
  const adminDb = testEnv.authenticatedContext(ADMIN_UID).firestore();
  await assertSucceeds(
    adminDb.collection("schemes").doc("pm_kisan").set({
      schemeName: "PM Kisan Samman Nidhi",
      schemeNameHindi: "पीएम किसान सम्मान निधि",
      category: "Agriculture",
      department: "Agriculture",
      isPublished: true,
      createdAt: new Date(),
      updatedAt: new Date(),
    })
  );
});

test("RBAC 4: Citizen cannot read or modify audit logs (Super Admin only)", async () => {
  const citizenDb = testEnv.authenticatedContext(CITIZEN_UID).firestore();
  await assertFails(citizenDb.collection("audit_logs").doc("log_1").get());
  await assertFails(citizenDb.collection("audit_logs").get());
});

test("RBAC 5: Super Admin CAN read and inspect audit logs", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("audit_logs").doc("log_1").set({
      actorId: ADMIN_UID,
      actorRole: "SUPER_ADMIN",
      action: "UPDATE_SCHEME",
      timestamp: new Date(),
    });
  });

  const adminDb = testEnv.authenticatedContext(ADMIN_UID).firestore();
  await assertSucceeds(adminDb.collection("audit_logs").doc("log_1").get());
});

test("RBAC 6: Citizen cannot read another citizen's application case", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("application_cases").doc("bob_case").set({
      userId: OTHER_CITIZEN_UID,
      schemeId: "pm_kisan",
      schemeName: "PM Kisan",
      status: "Submitted",
      panchayat: "Nohsa",
      createdAt: new Date(),
      updatedAt: new Date(),
    });
  });

  const citizenDb = testEnv.authenticatedContext(CITIZEN_UID).firestore();
  await assertFails(citizenDb.collection("application_cases").doc("bob_case").get());
});

test("RBAC 7: Operator CAN access citizen application cases in assigned jurisdiction (Nohsa)", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("application_cases").doc("nohsa_case").set({
      userId: OTHER_CITIZEN_UID,
      schemeId: "pm_kisan",
      schemeName: "PM Kisan",
      status: "Submitted",
      panchayat: "Nohsa",
      district: "Patna",
      createdAt: new Date(),
      updatedAt: new Date(),
    });
  });

  const operatorDb = testEnv.authenticatedContext(OPERATOR_UID).firestore();
  await assertSucceeds(operatorDb.collection("application_cases").doc("nohsa_case").get());
});

test("RBAC 8: Operator CANNOT access citizen application cases in another jurisdiction (Danapur)", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("application_cases").doc("danapur_case").set({
      userId: OTHER_CITIZEN_UID,
      schemeId: "pm_kisan",
      schemeName: "PM Kisan",
      status: "Submitted",
      panchayat: "Danapur_Cantonment",
      district: "Patna",
      createdAt: new Date(),
      updatedAt: new Date(),
    });
  });

  const operatorDb = testEnv.authenticatedContext(OPERATOR_UID).firestore();
  await assertFails(operatorDb.collection("application_cases").doc("danapur_case").get());
});

test("RBAC 9: Suspended Operator CANNOT access cases even in formerly assigned jurisdiction", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("application_cases").doc("nohsa_case").set({
      userId: OTHER_CITIZEN_UID,
      schemeId: "pm_kisan",
      schemeName: "PM Kisan",
      status: "Submitted",
      panchayat: "Nohsa",
      district: "Patna",
      createdAt: new Date(),
      updatedAt: new Date(),
    });
  });

  const suspendedDb = testEnv.authenticatedContext(SUSPENDED_OPERATOR_UID).firestore();
  await assertFails(suspendedDb.collection("application_cases").doc("nohsa_case").get());
});

test("RBAC 10: Operator CANNOT elevate permissions or alter user_roles", async () => {
  const operatorDb = testEnv.authenticatedContext(OPERATOR_UID).firestore();
  await assertFails(
    operatorDb.collection("user_roles").doc(OPERATOR_UID).update({
      role: "SUPER_ADMIN",
    })
  );
});
