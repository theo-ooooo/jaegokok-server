/* 재고콕 — Scan (입출고) + Employees */

/* ============================================================
   SCAN / 입출고
   ============================================================ */
function ScanScreen({ products, mobile, onRecord }) {
  const [mode, setMode] = React.useState("out");      // in | out
  const [phase, setPhase] = React.useState("idle");    // idle | scanning | result | done
  const [prod, setProd] = React.useState(null);
  const [qty, setQty] = React.useState(1);
  const [last, setLast] = React.useState(null);

  const startScan = () => {
    setPhase("scanning");
    setTimeout(() => {
      const p = products[Math.floor(Math.random() * products.length)];
      setProd(p); setQty(1); setPhase("result");
    }, 1400);
  };
  const confirm = () => {
    onRecord(prod, mode, qty);
    setLast({ name: prod.name, mode, qty, newStock: mode === "in" ? prod.stock + qty : Math.max(0, prod.stock - qty) });
    setPhase("done");
  };
  const reset = () => { setPhase("idle"); setProd(null); };

  const accent = mode === "in" ? "var(--ok)" : "var(--danger)";
  const accentTint = mode === "in" ? "var(--ok-tint)" : "var(--danger-tint)";

  const Toggle = () => (
    <div style={{ display: "flex", background: "var(--bg-alt)", border: "1px solid var(--border)", borderRadius: "var(--r-3)", padding: 4, gap: 4 }}>
      {[["in","입고",<IconIn size={18}/>],["out","출고",<IconOut size={18}/>]].map(([m,label,ic]) => (
        <button key={m} onClick={()=>{setMode(m); if(phase==="done") reset();}}
          style={{ flex: 1, display: "flex", alignItems: "center", justifyContent: "center", gap: 6, padding: "11px 10px",
            border: 0, borderRadius: "var(--r-2)", cursor: "pointer", fontWeight: 700, fontSize: 15,
            background: mode===m ? (m==="in"?"var(--ok)":"var(--danger)") : "transparent",
            color: mode===m ? "#fff" : "var(--text-secondary)", transition: "all .18s" }}>
          {ic}{label}
        </button>
      ))}
    </div>
  );

  return (
    <div style={{ maxWidth: 460, margin: "0 auto", display: "grid", gap: 18 }}>
      <Toggle/>

      {/* viewport */}
      {(phase === "idle" || phase === "scanning") && (
        <div style={{ position: "relative", aspectRatio: "1/1", background: "#0B0D12", borderRadius: "var(--r-3)", overflow: "hidden",
          display: "flex", alignItems: "center", justifyContent: "center" }}>
          {/* corner brackets */}
          {[[0,0,0,0],[0,"auto","auto",0],["auto",0,0,"auto"],["auto","auto","auto","auto"]].map((c,i)=>(
            <span key={i} style={{ position: "absolute", width: 38, height: 38,
              top: i<2?22:"auto", bottom: i>=2?22:"auto", left: i%2===0?22:"auto", right: i%2===1?22:"auto",
              borderTop: i<2?`3px solid ${accent}`:"none", borderBottom: i>=2?`3px solid ${accent}`:"none",
              borderLeft: i%2===0?`3px solid ${accent}`:"none", borderRight: i%2===1?`3px solid ${accent}`:"none",
              borderTopLeftRadius: i===0?6:0, borderTopRightRadius: i===1?6:0, borderBottomLeftRadius: i===2?6:0, borderBottomRightRadius: i===3?6:0 }}/>
          ))}
          {phase === "scanning" && <div className="jk-scanline" style={{ background: `linear-gradient(90deg,transparent,${accent},transparent)` }}/>}
          <div style={{ textAlign: "center", color: "rgba(255,255,255,.85)" }}>
            <div style={{ opacity: .9, color: accent }}><IconQr size={64}/></div>
            <div style={{ marginTop: 14, fontSize: 14, color: "rgba(255,255,255,.6)" }}>
              {phase === "scanning" ? "QR 인식 중…" : "QR 코드를 사각형 안에 맞춰 주세요"}
            </div>
          </div>
        </div>
      )}

      {/* result */}
      {phase === "result" && prod && (
        <div className="card" style={{ padding: 22, animation: "jk-pop .25s var(--ease-out)" }}>
          <div style={{ display: "flex", alignItems: "center", gap: 8, color: accent, marginBottom: 14 }}>
            <IconCheck size={18}/><span style={{ fontWeight: 700, fontSize: 13, letterSpacing: ".04em" }}>인식 완료</span>
          </div>
          <div style={{ fontWeight: 800, fontSize: 20, color: "var(--text-primary)" }}>{prod.name}</div>
          <div className="t-mono" style={{ marginTop: 4 }}>{prod.sku} · 현재 재고 {prod.stock}</div>

          {/* stepper */}
          <div style={{ margin: "22px 0", display: "flex", alignItems: "center", justifyContent: "center", gap: 18 }}>
            <button className="btn btn--ghost" style={{ width: 52, height: 52, padding: 0, borderRadius: "50%" }}
              onClick={()=>setQty(q=>Math.max(1,q-1))}><IconMinus size={22}/></button>
            <div style={{ minWidth: 90, textAlign: "center" }}>
              <div style={{ fontSize: 40, fontWeight: 900, lineHeight: 1, color: "var(--text-primary)", letterSpacing: "-.03em" }}>{qty}</div>
              <div style={{ fontSize: 12, color: "var(--text-tertiary)", marginTop: 4 }}>개 {mode==="in"?"입고":"출고"}</div>
            </div>
            <button className="btn btn--ghost" style={{ width: 52, height: 52, padding: 0, borderRadius: "50%" }}
              onClick={()=>setQty(q=>q+1)}><IconPlus size={22}/></button>
          </div>

          {mode === "out" && qty > prod.stock && (
            <div className="field-error" style={{ textAlign: "center", marginBottom: 14 }}>재고({prod.stock})보다 많이 출고할 수 없습니다.</div>
          )}

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1.6fr", gap: 10 }}>
            <button className="btn btn--ghost" onClick={reset}>취소</button>
            <button className="btn" disabled={mode==="out" && qty>prod.stock}
              style={{ background: accent, borderColor: accent }} onClick={confirm}>
              {mode==="in"?"입고":"출고"} {qty}개 기록
            </button>
          </div>
        </div>
      )}

      {/* done */}
      {phase === "done" && last && (
        <div className="card" style={{ padding: 28, textAlign: "center", animation: "jk-pop .25s var(--ease-out)" }}>
          <div className="jk-checkpop" style={{ width: 76, height: 76, margin: "0 auto 18px", borderRadius: "50%",
            background: accentTint, color: accent, display: "flex", alignItems: "center", justifyContent: "center" }}>
            <IconCheck size={40}/>
          </div>
          <div style={{ fontWeight: 800, fontSize: 20, color: "var(--text-primary)" }}>기록 완료</div>
          <p className="t-body" style={{ margin: "8px 0 0", fontSize: 14 }}>
            {last.name} <strong style={{ color: accent }}>{last.mode==="in"?"+":"−"}{last.qty}</strong>
          </p>
          <div style={{ margin: "18px 0", padding: "14px 18px", background: "var(--bg-alt)", borderRadius: "var(--r-2)", display: "flex", justifyContent: "space-between" }}>
            <span className="t-body" style={{ margin: 0, fontSize: 14 }}>변경 후 재고</span>
            <span style={{ fontWeight: 800, color: "var(--text-primary)" }}>{last.newStock}개</span>
          </div>
          <button className="btn btn--block btn--lg" onClick={reset}><IconScan size={20}/>계속 스캔</button>
        </div>
      )}

      {/* idle CTA */}
      {phase === "idle" && (
        <button className="btn btn--block btn--lg" style={{ background: accent, borderColor: accent }} onClick={startScan}>
          <IconScan size={20}/>스캔 시작
        </button>
      )}
      {phase === "scanning" && (
        <button className="btn btn--block btn--lg" disabled><span className="jk-spin" style={{display:"inline-block"}}>◌</span> 인식 중…</button>
      )}
    </div>
  );
}

/* ============================================================
   EMPLOYEES / 직원 관리
   ============================================================ */
function EmployeesScreen({ employees, mobile, onAdd, toast }) {
  const [open, setOpen] = React.useState(false);
  const [form, setForm] = React.useState({ name: "", email: "", role: "EMPLOYEE" });
  const [err, setErr] = React.useState({});

  const submit = () => {
    const e = {};
    if (!form.name.trim()) e.name = "이름을 입력하세요.";
    if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(form.email)) e.email = "올바른 이메일을 입력하세요.";
    setErr(e);
    if (Object.keys(e).length) return;
    onAdd({ ...form, name: form.name.trim() });
    toast(`${form.name.trim()}님에게 초대를 보냈습니다`);
    setForm({ name: "", email: "", role: "EMPLOYEE" }); setOpen(false);
  };

  const roleBadge = (r) => r === "OWNER" ? <Badge tone="brand">OWNER</Badge> : <Badge>EMPLOYEE</Badge>;
  const statusBadge = (s) => s === "active" ? <Badge tone="ok">활성</Badge> : <Badge tone="low">초대됨</Badge>;

  return (
    <div style={{ display: "grid", gap: mobile ? 14 : 20 }}>
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 12 }}>
        <p className="t-body" style={{ margin: 0, fontSize: 14 }}>{employees.length}명 · 사장님이 직원 계정을 만들고 권한을 관리합니다.</p>
        <button className="btn" onClick={()=>setOpen(true)}><IconPlus size={18}/>직원 추가</button>
      </div>

      {!mobile && (
        <div className="card" style={{ padding: 0, overflow: "hidden" }}>
          <table className="tbl">
            <thead><tr><th>이름</th><th>이메일</th><th>권한</th><th>상태</th><th>마지막 활동</th><th></th></tr></thead>
            <tbody>
              {employees.map(e => (
                <tr key={e.id}>
                  <td><div style={{ display: "flex", alignItems: "center", gap: 12 }}><Avatar name={e.name} color={e.color}/><span className="cell-primary">{e.name}</span></div></td>
                  <td className="t-mono" style={{ color: "var(--text-secondary)" }}>{e.email}</td>
                  <td>{roleBadge(e.role)}</td>
                  <td>{statusBadge(e.status)}</td>
                  <td>{e.last}</td>
                  <td style={{ textAlign: "right" }}><button className="btn btn--quiet" style={{ padding: 6, minHeight: 0 }} onClick={()=>toast("관리 메뉴")}><IconDots size={20}/></button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {mobile && (
        <div style={{ display: "grid", gap: 12 }}>
          {employees.map(e => (
            <div key={e.id} className="card" style={{ padding: 16, display: "flex", alignItems: "center", gap: 14 }}>
              <Avatar name={e.name} color={e.color} size={44}/>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ display: "flex", alignItems: "center", gap: 8 }}><span style={{ fontWeight: 700, color: "var(--text-primary)" }}>{e.name}</span>{roleBadge(e.role)}</div>
                <div className="t-mono" style={{ marginTop: 4, whiteSpace:"nowrap", overflow:"hidden", textOverflow:"ellipsis" }}>{e.email}</div>
                <div style={{ marginTop: 8 }}>{statusBadge(e.status)} <span style={{ color: "var(--text-tertiary)", fontSize: 12 }}>· {e.last}</span></div>
              </div>
            </div>
          ))}
        </div>
      )}

      {open && (
        <Modal title="직원 추가" onClose={()=>setOpen(false)}>
          <div style={{ display: "grid", gap: 16 }}>
            <div className="field">
              <label>이름</label>
              <input className={"input" + (err.name?" input--error":"")} value={form.name}
                onChange={e=>setForm(f=>({...f,name:e.target.value}))} placeholder="예: 김민재"/>
              {err.name && <span className="field-error">{err.name}</span>}
            </div>
            <div className="field">
              <label>이메일 (초대 링크 발송)</label>
              <div className="input-icon">
                <IconMail size={18}/>
                <input className={"input" + (err.email?" input--error":"")} value={form.email}
                  onChange={e=>setForm(f=>({...f,email:e.target.value}))} placeholder="name@store.kr"/>
              </div>
              {err.email && <span className="field-error">{err.email}</span>}
            </div>
            <div className="field">
              <label>권한</label>
              <div style={{ display: "flex", gap: 10 }}>
                {[["EMPLOYEE","직원","스캔·입출고만"],["OWNER","사장님","전체 관리"]].map(([v,t,d])=>(
                  <button key={v} onClick={()=>setForm(f=>({...f,role:v}))}
                    style={{ flex: 1, textAlign: "left", padding: "12px 14px", cursor: "pointer",
                      border: "1px solid " + (form.role===v?"var(--brand)":"var(--border-strong)"),
                      background: form.role===v?"var(--bg-tint)":"#fff", borderRadius: "var(--r-2)" }}>
                    <div style={{ fontWeight: 700, color: form.role===v?"var(--brand)":"var(--text-primary)" }}>{t}</div>
                    <div style={{ fontSize: 12, color: "var(--text-tertiary)", marginTop: 2 }}>{d}</div>
                  </button>
                ))}
              </div>
            </div>
            <div style={{ display: "flex", gap: 10, marginTop: 4 }}>
              <button className="btn btn--ghost btn--block" onClick={()=>setOpen(false)}>취소</button>
              <button className="btn btn--block" onClick={submit}>초대 보내기</button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
}

Object.assign(window, { ScanScreen, EmployeesScreen });
