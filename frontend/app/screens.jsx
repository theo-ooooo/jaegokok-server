/* 재고콕 — shared atoms + 4 screens */

/* ---------- atoms ---------- */
function Badge({ tone, children }) {
  const cls = tone === "ok" ? "badge badge--ok" : tone === "low" ? "badge badge--warn"
    : tone === "out" ? "badge badge--danger" : tone === "brand" ? "badge badge--brand" : "badge";
  return <span className={cls}><span className="dot"></span>{children}</span>;
}
const stockBadge = (p) => {
  const s = stockState(p);
  return <Badge tone={s}>{s === "ok" ? "양호" : s === "low" ? "저재고" : "품절"}</Badge>;
};

function Avatar({ name, color, size = 38 }) {
  return <span style={{ width: size, height: size, flex: "none", borderRadius: "50%", background: (color||"#3A5BD9") + "1A",
    color: color || "#3A5BD9", display: "inline-flex", alignItems: "center", justifyContent: "center",
    fontWeight: 800, fontSize: size * 0.4 }}>{name[0]}</span>;
}

/* deterministic pseudo-QR */
function QrPattern({ seed = 1, size = 160 }) {
  const N = 21, cell = size / N;
  let s = seed * 9301 + 49297;
  const rnd = () => { s = (s * 9301 + 49297) % 233280; return s / 233280; };
  const finder = (x, y) => (x < 7 && y < 7) || (x >= N - 7 && y < 7) || (x < 7 && y >= N - 7);
  const cells = [];
  for (let y = 0; y < N; y++) for (let x = 0; x < N; x++) {
    if (finder(x, y)) continue;
    if (rnd() > 0.52) cells.push(<rect key={x+"-"+y} x={x*cell} y={y*cell} width={cell} height={cell} fill="#0B0D12"/>);
  }
  const Finder = ({ x, y }) => (<g>
    <rect x={x*cell} y={y*cell} width={cell*7} height={cell*7} fill="#0B0D12"/>
    <rect x={(x+1)*cell} y={(y+1)*cell} width={cell*5} height={cell*5} fill="#fff"/>
    <rect x={(x+2)*cell} y={(y+2)*cell} width={cell*3} height={cell*3} fill="#0B0D12"/>
  </g>);
  return (
    <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`} style={{ display: "block" }}>
      <rect width={size} height={size} fill="#fff"/>
      {cells}
      <Finder x={0} y={0}/><Finder x={N-7} y={0}/><Finder x={0} y={N-7}/>
    </svg>
  );
}

function Modal({ title, onClose, children, wide }) {
  return (
    <div onClick={onClose} style={{ position: "absolute", inset: 0, background: "rgba(11,13,18,.45)",
      display: "flex", alignItems: "center", justifyContent: "center", padding: 20, zIndex: 60, animation: "jk-fade .18s ease" }}>
      <div onClick={(e) => e.stopPropagation()} style={{ background: "#fff", border: "1px solid var(--border)",
        borderRadius: "var(--r-3)", width: wide ? 460 : 380, maxWidth: "100%", maxHeight: "100%", overflow: "auto",
        boxShadow: "var(--shadow-pop)", animation: "jk-pop .22s var(--ease-out)" }}>
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between",
          padding: "18px 22px", borderBottom: "1px solid var(--border)" }}>
          <h3 className="t-h3" style={{ margin: 0 }}>{title}</h3>
          <button className="btn btn--quiet" style={{ padding: 6, minHeight: 0 }} onClick={onClose}><IconX size={20}/></button>
        </div>
        <div style={{ padding: 22 }}>{children}</div>
      </div>
    </div>
  );
}

/* ============================================================
   DASHBOARD
   ============================================================ */
function DashScreen({ products, activity, mobile, go }) {
  const lows = products.filter(p => stockState(p) !== "ok");
  const todayIn = activity.filter(a => a.type === "in").reduce((s,a)=>s+a.qty,0);
  const todayOut = activity.filter(a => a.type === "out").reduce((s,a)=>s+a.qty,0);
  const asset = products.reduce((s,p)=>s+p.stock*p.price,0);

  const Stat = ({ label, value, unit, foot, footTone }) => (
    <div className="card" style={{ padding: mobile ? 16 : 24 }}>
      <span className="t-label">{label}</span>
      <div style={{ display: "flex", alignItems: "baseline", gap: 6, marginTop: 10 }}>
        <span className="t-h1" style={{ fontSize: mobile ? 24 : 30 }}>{value}</span>
        {unit && <span className="t-body" style={{ margin: 0, fontSize: 13 }}>{unit}</span>}
      </div>
      {foot && <div className={"badge " + (footTone||"")} style={{ marginTop: 12 }}><span className="dot"></span>{foot}</div>}
    </div>
  );

  return (
    <div style={{ display: "grid", gap: mobile ? 16 : 24 }}>
      <div style={{ display: "grid", gridTemplateColumns: mobile ? "1fr 1fr" : "repeat(4,1fr)", gap: mobile ? 12 : 20 }}>
        <Stat label="총 상품" value={products.length} unit="종" foot={`+${todayIn} 입고 today`} footTone="badge--ok"/>
        <Stat label="오늘 입출고" value={activity.filter(a=>a.at!=="어제").length} unit="건" foot={`입고 ${todayIn} · 출고 ${todayOut}`} footTone="badge--brand"/>
        <Stat label="저재고 알림" value={lows.length} unit="건" foot={lows.length? "확인 필요" : "이상 없음"} footTone={lows.length?"badge--warn":"badge--ok"}/>
        <Stat label="재고 자산" value={"₩"+(asset/10000).toFixed(0)} unit="만원" foot="실시간" footTone="badge--brand"/>
      </div>

      <div style={{ display: "grid", gridTemplateColumns: mobile ? "1fr" : "1fr 1fr", gap: mobile ? 16 : 24 }}>
        {/* low stock */}
        <div className="card" style={{ padding: 0, overflow: "hidden" }}>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", padding: "18px 22px", borderBottom: "1px solid var(--border)" }}>
            <div style={{ display: "flex", alignItems: "center", gap: 10 }}><span style={{color:"var(--warn)"}}><IconBell size={18}/></span><h3 className="t-h3" style={{ margin: 0 }}>저재고 알림</h3></div>
            <button className="btn btn--quiet btn--sm" onClick={()=>go("products")}>전체보기<IconChevR size={16}/></button>
          </div>
          <div>
            {lows.map(p => (
              <div key={p.id} style={{ display: "flex", alignItems: "center", gap: 12, padding: "14px 22px", borderBottom: "1px solid var(--border)" }}>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontWeight: 600, color: "var(--text-primary)", whiteSpace:"nowrap", overflow:"hidden", textOverflow:"ellipsis" }}>{p.name}</div>
                  <div className="t-mono" style={{ marginTop: 2 }}>{p.sku} · 안전재고 {p.safe}</div>
                </div>
                <div style={{ textAlign: "right" }}>
                  <div style={{ fontWeight: 800, fontSize: 18, color: stockState(p)==="out"?"var(--danger)":"var(--warn)" }}>{p.stock}</div>
                </div>
                {stockBadge(p)}
              </div>
            ))}
            {!lows.length && <div style={{ padding: 28, textAlign: "center", color: "var(--text-tertiary)" }}>저재고 항목이 없습니다 👍</div>}
          </div>
        </div>

        {/* activity */}
        <div className="card" style={{ padding: 0, overflow: "hidden" }}>
          <div style={{ display: "flex", alignItems: "center", gap: 10, padding: "18px 22px", borderBottom: "1px solid var(--border)" }}>
            <span style={{color:"var(--brand)"}}><IconClock size={18}/></span><h3 className="t-h3" style={{ margin: 0 }}>최근 입출고</h3>
          </div>
          <div>
            {activity.map((a, i) => (
              <div key={i} style={{ display: "flex", alignItems: "center", gap: 12, padding: "13px 22px", borderBottom: "1px solid var(--border)" }}>
                <span style={{ width: 32, height: 32, flex:"none", borderRadius: "var(--r-2)", display: "inline-flex", alignItems: "center", justifyContent: "center",
                  background: a.type==="in"?"var(--ok-tint)":"var(--danger-tint)", color: a.type==="in"?"var(--ok)":"var(--danger)" }}>
                  {a.type==="in" ? <IconIn size={18}/> : <IconOut size={18}/>}
                </span>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontWeight: 600, color: "var(--text-primary)", whiteSpace:"nowrap", overflow:"hidden", textOverflow:"ellipsis" }}>{a.name}</div>
                  <div className="t-mono" style={{ marginTop: 2 }}>{a.who}</div>
                </div>
                <div style={{ textAlign: "right" }}>
                  <div style={{ fontWeight: 800, color: a.type==="in"?"var(--ok)":"var(--danger)" }}>{a.type==="in"?"+":"−"}{a.qty}</div>
                  <div className="t-mono">{a.at}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

/* ============================================================
   PRODUCTS
   ============================================================ */
function ProductsScreen({ products, mobile, toast }) {
  const [q, setQ] = React.useState("");
  const [cat, setCat] = React.useState("전체");
  const [qr, setQr] = React.useState(null);
  const cats = ["전체", "원두", "부자재", "포장재"];
  const list = products.filter(p =>
    (cat === "전체" || p.cat === cat) &&
    (p.name.toLowerCase().includes(q.toLowerCase()) || p.sku.toLowerCase().includes(q.toLowerCase())));

  return (
    <div style={{ display: "grid", gap: mobile ? 14 : 20 }}>
      {/* toolbar */}
      <div style={{ display: "flex", gap: 12, flexWrap: "wrap", alignItems: "center" }}>
        <div className="input-icon" style={{ flex: 1, minWidth: mobile ? "100%" : 240 }}>
          <IconSearch size={18}/>
          <input className="input" placeholder="상품 · SKU 검색" value={q} onChange={e=>setQ(e.target.value)}/>
        </div>
        {!mobile && (
          <div style={{ display: "flex", gap: 8 }}>
            {cats.map(c => (
              <button key={c} className={"btn btn--sm " + (cat===c?"":"btn--ghost")} onClick={()=>setCat(c)}>{c}</button>
            ))}
          </div>
        )}
        {mobile && (
          <select className="select" style={{ width: "auto", flex: 1 }} value={cat} onChange={e=>setCat(e.target.value)}>
            {cats.map(c => <option key={c}>{c}</option>)}
          </select>
        )}
        <button className="btn" onClick={()=>toast("상품 추가 화면으로 이동")}><IconPlus size={18}/>상품 추가</button>
      </div>

      {/* desktop table */}
      {!mobile && (
        <div className="card" style={{ padding: 0, overflow: "hidden" }}>
          <table className="tbl">
            <thead><tr><th>상품명</th><th>SKU</th><th>카테고리</th><th>재고</th><th>상태</th><th style={{textAlign:"right"}}>QR</th></tr></thead>
            <tbody>
              {list.map(p => (
                <tr key={p.id}>
                  <td className="cell-primary">{p.name}</td>
                  <td className="t-mono" style={{ color: "var(--text-secondary)" }}>{p.sku}</td>
                  <td>{p.cat}</td>
                  <td className="cell-primary">{p.stock} <span style={{color:"var(--text-tertiary)",fontWeight:400}}>/ {p.safe}</span></td>
                  <td>{stockBadge(p)}</td>
                  <td style={{ textAlign: "right" }}>
                    <button className="btn btn--ghost btn--sm" onClick={()=>setQr(p)}><IconQr size={16}/>QR</button>
                  </td>
                </tr>
              ))}
              {!list.length && <tr><td colSpan="6" style={{ textAlign: "center", color: "var(--text-tertiary)", padding: 36 }}>검색 결과가 없습니다.</td></tr>}
            </tbody>
          </table>
        </div>
      )}

      {/* mobile cards */}
      {mobile && (
        <div style={{ display: "grid", gap: 12 }}>
          {list.map(p => (
            <div key={p.id} className="card" style={{ padding: 16, display: "flex", alignItems: "center", gap: 14 }}>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontWeight: 600, color: "var(--text-primary)" }}>{p.name}</div>
                <div className="t-mono" style={{ marginTop: 3 }}>{p.sku} · {p.cat}</div>
                <div style={{ display: "flex", alignItems: "center", gap: 8, marginTop: 10 }}>
                  {stockBadge(p)}<span style={{ fontWeight: 800 }}>{p.stock}</span><span style={{ color: "var(--text-tertiary)", fontSize: 13 }}>/ {p.safe}</span>
                </div>
              </div>
              <button className="btn btn--ghost btn--sm" style={{ flexDirection: "column", gap: 2, height: "auto", padding: "10px 12px" }} onClick={()=>setQr(p)}>
                <IconQr size={20}/><span style={{ fontSize: 11 }}>QR</span>
              </button>
            </div>
          ))}
          {!list.length && <div className="card" style={{ textAlign: "center", color: "var(--text-tertiary)" }}>검색 결과가 없습니다.</div>}
        </div>
      )}

      {qr && (
        <Modal title="QR 코드" onClose={()=>setQr(null)}>
          <div style={{ textAlign: "center" }}>
            <div style={{ fontWeight: 700, color: "var(--text-primary)" }}>{qr.name}</div>
            <div className="t-mono" style={{ marginTop: 4, marginBottom: 18 }}>{qr.sku}</div>
            <div style={{ display: "inline-block", padding: 16, border: "1px solid var(--border)", borderRadius: "var(--r-3)" }}>
              <QrPattern seed={parseInt(qr.sku.replace(/\D/g,""))||7} size={176}/>
            </div>
            <p className="t-body" style={{ fontSize: 13, margin: "16px 0 18px" }}>이 QR을 출력해 상품에 부착하세요. 직원이 스캔하면 자동으로 입출고가 기록됩니다.</p>
            <div style={{ display: "flex", gap: 10 }}>
              <button className="btn btn--ghost btn--block" onClick={()=>{toast("인쇄 대화상자 열림");}}>인쇄</button>
              <button className="btn btn--block" onClick={()=>{toast(qr.sku+" QR 다운로드됨"); setQr(null);}}><IconDownload size={18}/>다운로드</button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
}

Object.assign(window, { Badge, stockBadge, Avatar, QrPattern, Modal, DashScreen, ProductsScreen });
