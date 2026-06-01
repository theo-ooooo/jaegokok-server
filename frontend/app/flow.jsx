/* 재고콕 — Full flow: Auth → Onboarding → App */

/* ---------- AUTH (signup ⇄ login) ---------- */
function AuthField({ label, children, e }) {
  return <div className="field"><label>{label}</label>{children}{e && <span className="field-error">{e}</span>}</div>;
}
function AuthFlow({ onSignup, onLogin }) {
  const [tab, setTab] = React.useState("signup");
  const [f, setF] = React.useState({ name: "", biz: "", email: "", pw: "" });
  const [le, setLe] = React.useState({ email: "강경원@store.kr", pw: "demo1234" });
  const [err, setErr] = React.useState({});
  const reEmail = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;

  const submitSignup = () => {
    const e = {};
    if (!f.name.trim()) e.name = "이름을 입력하세요.";
    if (!f.biz.trim()) e.biz = "매장명을 입력하세요.";
    if (!reEmail.test(f.email.trim())) e.email = "올바른 이메일을 입력하세요.";
    if (f.pw.length < 6) e.pw = "6자 이상 입력하세요.";
    setErr(e);
    if (Object.keys(e).length) return;
    onSignup({ name: f.name.trim(), biz: f.biz.trim(), email: f.email.trim() });
  };

  const Logo = (
    <div style={{ display: "flex", alignItems: "center", gap: 11, justifyContent: "center", marginBottom: 22 }}>
      <svg width="34" height="34" viewBox="0 0 64 64"><rect x="7" y="7" width="50" height="50" rx="13" fill="var(--brand)"/><path d="M19 33 l9 9 l17 -19" fill="none" stroke="#fff" strokeWidth="5.5" strokeLinecap="round" strokeLinejoin="round"/></svg>
      <span style={{ fontWeight: 900, fontSize: 21, letterSpacing: "-.02em" }}>재고콕<span style={{ color: "var(--brand)" }}>.</span></span>
    </div>
  );
  const F = AuthField;

  return (
    <div style={{ minHeight: "100%", display: "flex", flexDirection: "column", justifyContent: "center", padding: "32px 24px", background: "var(--bg)" }}>
      <div style={{ width: "100%", maxWidth: 360, margin: "0 auto" }}>
        {Logo}
        <div className="roletabs" style={{ display: "flex", gap: 6, background: "var(--bg-alt)", border: "1px solid var(--border)", padding: 5, borderRadius: "var(--r-3)", marginBottom: 22 }}>
          {[["signup","회원가입"],["login","로그인"]].map(([v,t]) => (
            <button key={v} onClick={()=>{setTab(v); setErr({});}} style={{ flex: 1, padding: "10px", border: 0, cursor: "pointer", borderRadius: "var(--r-2)", fontFamily: "var(--font-sans)", fontWeight: 700, fontSize: 14,
              background: tab===v?"var(--bg-card)":"transparent", color: tab===v?"var(--brand)":"var(--text-secondary)", boxShadow: tab===v?"var(--shadow-rest)":"none", transition: "all .18s" }}>{t}</button>
          ))}
        </div>

        {tab === "signup" ? (
          <div style={{ display: "grid", gap: 15 }}>
            <div>
              <h1 style={{ fontSize: 20, fontWeight: 900, margin: "0 0 4px", letterSpacing: "-.02em" }}>사업장 등록 시작</h1>
              <p style={{ fontSize: 13, color: "var(--text-secondary)", margin: 0 }}>사장님 계정을 만들고 30초 만에 시작하세요.</p>
            </div>
            <F label="대표자 이름" e={err.name}><input className={"input"+(err.name?" input--error":"")} value={f.name} onChange={e=>setF({...f,name:e.target.value})} placeholder="예: 강경원"/></F>
            <F label="매장명" e={err.biz}><input className={"input"+(err.biz?" input--error":"")} value={f.biz} onChange={e=>setF({...f,biz:e.target.value})} placeholder="예: 해와달 카페"/></F>
            <F label="이메일" e={err.email}><input className={"input"+(err.email?" input--error":"")} value={f.email} onChange={e=>setF({...f,email:e.target.value})} placeholder="owner@store.kr"/></F>
            <F label="비밀번호" e={err.pw}><input className={"input"+(err.pw?" input--error":"")} type="password" value={f.pw} onChange={e=>setF({...f,pw:e.target.value})} placeholder="6자 이상"/></F>
            <button className="btn btn--lg btn--block" onClick={submitSignup} style={{ marginTop: 4 }}>다음<IconChevR size={18}/></button>
            <p style={{ fontSize: 12, color: "var(--text-tertiary)", textAlign: "center", margin: "2px 0 0", lineHeight: 1.6 }}>가입 시 <span style={{color:"var(--brand)"}}>이용약관</span> 및 <span style={{color:"var(--brand)"}}>개인정보 처리방침</span>에 동의합니다.</p>
          </div>
        ) : (
          <div style={{ display: "grid", gap: 15 }}>
            <div>
              <h1 style={{ fontSize: 20, fontWeight: 900, margin: "0 0 4px", letterSpacing: "-.02em" }}>다시 오셨네요</h1>
              <p style={{ fontSize: 13, color: "var(--text-secondary)", margin: 0 }}>기존 매장 계정으로 로그인합니다.</p>
            </div>
            <F label="이메일"><div className="input-icon"><IconMail size={18}/><input className="input" value={le.email} onChange={e=>setLe({...le,email:e.target.value})}/></div></F>
            <F label="비밀번호"><input className="input" type="password" value={le.pw} onChange={e=>setLe({...le,pw:e.target.value})}/></F>
            <button className="btn btn--lg btn--block" onClick={onLogin} style={{ marginTop: 4 }}>로그인</button>
            <p style={{ fontSize: 12, color: "var(--text-tertiary)", textAlign: "center", margin: 0 }}>데모 계정이 입력되어 있어요 — 그대로 로그인하세요.</p>
          </div>
        )}
      </div>
    </div>
  );
}

/* ---------- ONBOARDING WIZARD ---------- */
const BIZ_TYPES = ["카페·음료", "음식점", "베이커리", "소매·편의", "미용·뷰티", "기타"];
const SUGGEST = {
  "카페·음료": [["콜드브루 원두 1kg","원두"],["우유 1L","부자재"],["종이컵 16oz","포장재"],["바닐라 시럽 750ml","부자재"]],
  "음식점": [["식용유 18L","식자재"],["일회용 용기 (500입)","포장재"],["나무젓가락 (1000입)","포장재"]],
  "베이커리": [["강력분 20kg","식자재"],["버터 2kg","식자재"],["포장 박스 소","포장재"]],
  "소매·편의": [["생수 500ml (20입)","음료"],["봉투 대 (100매)","포장재"],["영수증 용지","소모품"]],
  "미용·뷰티": [["샴푸 1L","제품"],["타월 (50매)","소모품"],["일회용 장갑","소모품"]],
  "기타": [["품목 A","일반"],["품목 B","일반"]],
};

function Onboarding({ owner, biz, onComplete }) {
  const [step, setStep] = React.useState(0);     // 0 biz, 1 products, 2 staff, 3 done
  const [type, setType] = React.useState("");
  const [items, setItems] = React.useState([]);  // {name,cat,stock,safe}
  const [draft, setDraft] = React.useState({ name: "", stock: "", safe: "" });
  const [invites, setInvites] = React.useState([]);
  const [inviteEmail, setInviteEmail] = React.useState("");

  const steps = ["사업장", "상품", "직원"];
  const addItem = (name, cat) => {
    if (!name.trim()) return;
    setItems(it => [...it, { name: name.trim(), cat: cat || "일반", stock: parseInt(draft.stock) || 0, safe: parseInt(draft.safe) || 5 }]);
    setDraft({ name: "", stock: "", safe: "" });
  };

  const Header = (
    <div style={{ padding: "20px 22px 0" }}>
      <div style={{ display: "flex", alignItems: "center", gap: 9, marginBottom: 18 }}>
        <svg width="26" height="26" viewBox="0 0 64 64"><rect x="7" y="7" width="50" height="50" rx="13" fill="var(--brand)"/><path d="M19 33 l9 9 l17 -19" fill="none" stroke="#fff" strokeWidth="6" strokeLinecap="round" strokeLinejoin="round"/></svg>
        <span style={{ fontWeight: 900, fontSize: 16, letterSpacing: "-.02em" }}>재고콕<span style={{ color: "var(--brand)" }}>.</span></span>
      </div>
      {step < 3 && (
        <div style={{ display: "flex", gap: 6, marginBottom: 4 }}>
          {steps.map((s, i) => (
            <div key={s} style={{ flex: 1, display: "grid", gap: 6 }}>
              <div style={{ height: 4, borderRadius: 999, background: i <= step ? "var(--brand)" : "var(--border-strong)", transition: "background .3s" }}/>
              <span style={{ fontSize: 11, fontWeight: 600, color: i === step ? "var(--brand)" : "var(--text-tertiary)" }}>{i+1}. {s}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );

  let body;
  if (step === 0) {
    body = (
      <div style={{ padding: "22px", display: "grid", gap: 18 }}>
        <div>
          <h1 style={{ fontSize: 22, fontWeight: 900, margin: "0 0 6px", letterSpacing: "-.02em" }}>{biz} 👋</h1>
          <p style={{ fontSize: 14, color: "var(--text-secondary)", margin: 0 }}>업종을 선택하면 자주 쓰는 품목을 추천해 드려요.</p>
        </div>
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
          {BIZ_TYPES.map(t => (
            <button key={t} onClick={()=>setType(t)} style={{ padding: "16px 14px", textAlign: "left", cursor: "pointer", borderRadius: "var(--r-3)",
              border: "1px solid " + (type===t?"var(--brand)":"var(--border-strong)"), background: type===t?"var(--bg-tint)":"var(--bg-card)",
              color: type===t?"var(--brand)":"var(--text-primary)", fontWeight: 700, fontSize: 14, transition: "all .15s", boxShadow: type===t?"none":"var(--shadow-rest)" }}>{t}</button>
          ))}
        </div>
        <button className="btn btn--lg btn--block" disabled={!type} onClick={()=>setStep(1)}>다음<IconChevR size={18}/></button>
      </div>
    );
  } else if (step === 1) {
    const sugg = (SUGGEST[type] || []).filter(s => !items.find(i => i.name === s[0]));
    body = (
      <div style={{ padding: "22px", display: "grid", gap: 16 }}>
        <div>
          <h1 style={{ fontSize: 22, fontWeight: 900, margin: "0 0 6px", letterSpacing: "-.02em" }}>첫 상품 등록</h1>
          <p style={{ fontSize: 14, color: "var(--text-secondary)", margin: 0 }}>지금 관리할 상품을 추가하세요. 나중에 더 추가할 수 있어요.</p>
        </div>

        {sugg.length > 0 && (
          <div>
            <span className="t-label" style={{ display: "block", marginBottom: 8 }}>추천 품목 · 탭하여 추가</span>
            <div style={{ display: "flex", flexWrap: "wrap", gap: 8 }}>
              {sugg.map(([n,c]) => (
                <button key={n} onClick={()=>addItem(n, c)} style={{ display: "inline-flex", alignItems: "center", gap: 6, padding: "8px 12px", borderRadius: "var(--r-pill)",
                  border: "1px solid var(--border-strong)", background: "var(--bg-card)", cursor: "pointer", fontSize: 13, fontWeight: 600, color: "var(--text-secondary)" }}>
                  <IconPlus size={15}/>{n}
                </button>
              ))}
            </div>
          </div>
        )}

        {/* custom add */}
        <div style={{ display: "grid", gap: 9, padding: 14, background: "var(--bg-alt)", borderRadius: "var(--r-3)", border: "1px solid var(--border)" }}>
          <input className="input" placeholder="상품명 직접 입력" value={draft.name} onChange={e=>setDraft({...draft,name:e.target.value})}/>
          <div style={{ display: "flex", gap: 9 }}>
            <input className="input" type="number" placeholder="현재 재고" value={draft.stock} onChange={e=>setDraft({...draft,stock:e.target.value})}/>
            <input className="input" type="number" placeholder="안전 재고" value={draft.safe} onChange={e=>setDraft({...draft,safe:e.target.value})}/>
          </div>
          <button className="btn btn--ghost btn--block" onClick={()=>addItem(draft.name)} disabled={!draft.name.trim()}><IconPlus size={17}/>목록에 추가</button>
        </div>

        {/* added list */}
        {items.length > 0 && (
          <div style={{ display: "grid", gap: 8 }}>
            <span className="t-label">{items.length}개 추가됨</span>
            {items.map((it, i) => (
              <div key={i} style={{ display: "flex", alignItems: "center", gap: 10, padding: "11px 14px", background: "var(--bg-card)", border: "1px solid var(--border)", borderRadius: "var(--r-2)" }}>
                <span style={{ color: "var(--ok)" }}><IconCheck size={17}/></span>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontWeight: 600, fontSize: 14 }}>{it.name}</div>
                  <div className="t-mono" style={{ fontSize: 11 }}>{it.cat} · 재고 {it.stock} / 안전 {it.safe}</div>
                </div>
                <button className="btn btn--quiet" style={{ padding: 5, minHeight: 0 }} onClick={()=>setItems(items.filter((_,x)=>x!==i))}><IconX size={16}/></button>
              </div>
            ))}
          </div>
        )}

        <button className="btn btn--lg btn--block" disabled={!items.length} onClick={()=>setStep(2)}>{items.length ? `${items.length}개 등록하고 계속` : "상품을 1개 이상 추가하세요"}<IconChevR size={18}/></button>
      </div>
    );
  } else if (step === 2) {
    body = (
      <div style={{ padding: "22px", display: "grid", gap: 16 }}>
        <div>
          <h1 style={{ fontSize: 22, fontWeight: 900, margin: "0 0 6px", letterSpacing: "-.02em" }}>직원 초대 <span style={{ fontSize: 13, fontWeight: 600, color: "var(--text-tertiary)" }}>(선택)</span></h1>
          <p style={{ fontSize: 14, color: "var(--text-secondary)", margin: 0 }}>직원이 스캔으로 입출고만 기록할 수 있어요. 나중에 추가해도 됩니다.</p>
        </div>
        <div style={{ display: "flex", gap: 8 }}>
          <div className="input-icon" style={{ flex: 1 }}><IconMail size={18}/><input className="input" placeholder="직원 이메일" value={inviteEmail} onChange={e=>setInviteEmail(e.target.value)}/></div>
          <button className="btn" onClick={()=>{ if(/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(inviteEmail)){ setInvites([...invites, inviteEmail]); setInviteEmail(""); } }}>초대</button>
        </div>
        {invites.length > 0 && (
          <div style={{ display: "grid", gap: 8 }}>
            {invites.map((em, i) => (
              <div key={i} style={{ display: "flex", alignItems: "center", gap: 10, padding: "11px 14px", background: "var(--bg-card)", border: "1px solid var(--border)", borderRadius: "var(--r-2)" }}>
                <Avatar name={em} color="#1E9E6A" size={30}/>
                <span style={{ flex: 1, fontSize: 13, color: "var(--text-secondary)" }}>{em}</span>
                <span className="badge badge--warn"><span className="dot"></span>초대됨</span>
              </div>
            ))}
          </div>
        )}
        <div style={{ display: "grid", gap: 9, marginTop: 4 }}>
          <button className="btn btn--lg btn--block" onClick={()=>{ setStep(3); onComplete({ type, items, invites }); }}>완료하고 시작하기</button>
          <button className="btn btn--quiet btn--block" onClick={()=>{ setStep(3); onComplete({ type, items, invites: [] }); }}>건너뛰기</button>
        </div>
      </div>
    );
  } else {
    body = (
      <div style={{ padding: "40px 26px", textAlign: "center", display: "flex", flexDirection: "column", justifyContent: "center", minHeight: "70%" }}>
        <div className="jk-checkpop" style={{ width: 84, height: 84, margin: "0 auto 22px", borderRadius: "50%", background: "var(--bg-tint)", color: "var(--brand)", display: "flex", alignItems: "center", justifyContent: "center" }}><IconCheck size={46}/></div>
        <h1 style={{ fontSize: 24, fontWeight: 900, margin: "0 0 10px", letterSpacing: "-.02em" }}>준비 완료!</h1>
        <p style={{ fontSize: 14, color: "var(--text-secondary)", margin: "0 0 8px", lineHeight: 1.6 }}>{biz}의 재고 관리가 시작됐습니다.<br/>상품 {items.length}개 · QR {items.length}개 발급 완료.</p>
        <div style={{ display: "flex", gap: 8, justifyContent: "center", margin: "18px 0 26px", flexWrap: "wrap" }}>
          {items.slice(0,3).map((it,i)=>(
            <div key={i} style={{ padding: 8, background: "#fff", border: "1px solid var(--border)", borderRadius: "var(--r-2)", boxShadow: "var(--shadow-rest)" }}>
              <QrPattern seed={it.name.length*7+i*13+3} size={64}/>
            </div>
          ))}
        </div>
        <button className="btn btn--lg btn--block" onClick={()=>onComplete.enter && onComplete.enter()} id="enterAppBtn">대시보드로 이동<IconChevR size={18}/></button>
      </div>
    );
  }

  return <div style={{ minHeight: "100%", background: step < 3 ? "var(--bg)" : "var(--bg-alt)", display: "flex", flexDirection: "column" }}>{Header}<div style={{ flex: 1 }}>{body}</div></div>;
}

/* ---------- ROOT ORCHESTRATOR ---------- */
function FlowRoot() {
  const [phase, setPhase] = React.useState("auth");   // auth | onboarding | app
  React.useEffect(() => { window.__currentPhase = phase; }, [phase]);
  const [owner, setOwner] = React.useState("강경원");
  const [biz, setBiz] = React.useState("해와달 카페");

  const [products, setProducts] = React.useState([]);
  const [employees, setEmployees] = React.useState([]);
  const [activity, setActivity] = React.useState([]);

  const startSignup = (info) => {
    setOwner(info.name); setBiz(info.biz);
    setEmployees([{ id: "E01", name: info.name, role: "OWNER", email: info.email, status: "active", last: "방금 전", color: "#3A5BD9" }]);
    setProducts([]); setActivity([]);
    setPhase("onboarding");
  };
  const startLogin = () => {
    // existing account → full sample data
    setOwner("강경원"); setBiz("해와달 카페");
    setProducts(PRODUCTS.map(p => ({ ...p })));
    setEmployees(EMPLOYEES.map(e => ({ ...e })));
    setActivity(ACTIVITY.map(a => ({ ...a })));
    setPhase("app");
  };

  const colors = ["#1E9E6A", "#C9871F", "#D6453F", "#5C636E", "#5383E8"];
  const completeOnboarding = (data) => {
    setProducts(data.items.map((it, i) => ({
      id: "P" + (i+1), name: it.name, sku: "SKU-" + String(10001 + i*7).slice(-5),
      cat: it.cat, stock: it.stock, safe: it.safe, price: 0,
    })));
    if (data.invites && data.invites.length) {
      setEmployees(es => [...es, ...data.invites.map((em, i) => ({
        id: "E" + (es.length + i + 1), name: em.split("@")[0], role: "EMPLOYEE", email: em,
        status: "invited", last: "초대됨", color: colors[i % colors.length],
      }))]);
    }
  };
  completeOnboarding.enter = () => setPhase("app");

  const store = { products, setProducts, employees, setEmployees, activity, setActivity, bizName: biz };

  if (phase === "auth") return <AuthFlow onSignup={startSignup} onLogin={startLogin}/>;
  if (phase === "onboarding") return <Onboarding owner={owner} biz={biz} onComplete={completeOnboarding}/>;
  return <App mode="mobile" store={store} ownerName={owner} startScreen="dash"/>;
}

Object.assign(window, { FlowRoot, AuthFlow, Onboarding });
