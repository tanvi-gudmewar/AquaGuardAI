import React, { useCallback, useEffect, useMemo, useState } from 'react';
import {
  Activity, AlertTriangle, Droplets, Gauge, Leaf, Play, Square,
  RefreshCw, Waves, CheckCircle2, CircleHelp, Database, Wifi
} from 'lucide-react';
import {
  Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip,
  XAxis, YAxis, BarChart, Bar
} from 'recharts';
import { api } from './api.js';

const fallbackReadings = [
  { label: '10:00', flowRateLpm: 2.1 }, { label: '10:05', flowRateLpm: 2.4 },
  { label: '10:10', flowRateLpm: 2.0 }, { label: '10:15', flowRateLpm: 2.3 },
  { label: '10:20', flowRateLpm: 5.8 }, { label: '10:25', flowRateLpm: 2.2 }
];

function Metric({ icon: Icon, label, value, note, tone = 'blue' }) {
  return <article className="metric-card">
    <div className={`metric-icon ${tone}`}><Icon size={20} /></div>
    <p className="metric-label">{label}</p>
    <div className="metric-value">{value ?? '—'}</div>
    <p className="metric-note">{note}</p>
  </article>;
}

function formatNumber(value, digits = 1) {
  return Number.isFinite(Number(value)) ? Number(value).toLocaleString(undefined, { maximumFractionDigits: digits }) : '—';
}

export default function App() {
  const [dashboard, setDashboard] = useState(null);
  const [readings, setReadings] = useState([]);
  const [leaks, setLeaks] = useState([]);
  const [tanks, setTanks] = useState([]);
  const [savings, setSavings] = useState(null);
  const [running, setRunning] = useState(false);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [activePage, setActivePage] = useState('Overview');
  const [zone, setZone] = useState('Kitchen');
  const [flow, setFlow] = useState('2.4');
  const [tankCapacity, setTankCapacity] = useState('1000');
  const [tankVolume, setTankVolume] = useState('720');

  const load = useCallback(async () => {
    setError('');
    try {
      const results = await Promise.allSettled([
        api.dashboard(), api.readings(), api.leaks(), api.tanks(), api.savings(), api.simulationStatus()
      ]);
      const [d, r, l, t, s, sim] = results;
      if (d.status === 'fulfilled') setDashboard(d.value);
      if (r.status === 'fulfilled') setReadings(r.value);
      if (l.status === 'fulfilled') setLeaks(l.value);
      if (t.status === 'fulfilled') setTanks(t.value);
      if (s.status === 'fulfilled') setSavings(s.value);
      if (sim.status === 'fulfilled') setRunning(Boolean(sim.value.running));
      const failed = results.filter(x => x.status === 'rejected');
      if (failed.length === results.length) throw failed[0].reason;
      if (failed.length) setError('Some panels could not load. Check that the backend and MySQL are running.');
    } catch (e) {
      setError(`${e.message}. Start Spring Boot at localhost:8080 and verify the database settings.`);
    }
  }, []);

  useEffect(() => {
    load();
    const timer = setInterval(load, 5000);
    return () => clearInterval(timer);
  }, [load]);

  const chartData = useMemo(() => {
    if (!readings?.length) return fallbackReadings;
    return readings.slice(-12).map((r, i) => ({
      label: r.timestamp ? new Date(r.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : `#${i + 1}`,
      flowRateLpm: Number(r.flowRateLpm ?? 0)
    }));
  }, [readings]);

  const tankChartData = useMemo(() => tanks.map(t => ({
    name: t.name || `Tank ${t.id}`,
    fillPercent: Number(t.currentVolumeL && t.capacityL ? (t.currentVolumeL / t.capacityL) * 100 : t.fillPercent || 0)
  })), [tanks]);

  async function action(fn, success) {
    setBusy(true); setNotice(''); setError('');
    try { await fn(); setNotice(success); await load(); }
    catch (e) { setError(e.message); }
    finally { setBusy(false); }
  }

  async function submitReading(e) {
    e.preventDefault();
    const rate = Number(flow);
    if (!Number.isFinite(rate) || rate < 0) { setError('Flow rate must be a non-negative number.'); return; }
    await action(() => api.createReading({
      zone, flowRateLpm: rate, tankLevelPercent: 0, source: 'SIMULATED'
    }), 'Reading saved successfully.');
  }

  async function createDemoTank(e) {
    e.preventDefault();
    const capacityL = Number(tankCapacity), currentVolumeL = Number(tankVolume);
    if (!(capacityL > 0) || currentVolumeL < 0 || currentVolumeL > capacityL) {
      setError('Enter a positive capacity and a volume between 0 and capacity.'); return;
    }
    await action(() => fetch(`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'}/tanks`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name: 'Demo Tank', capacityL, currentVolumeL, incomingFlowLpm: 5, outgoingFlowLpm: 1, warningThresholdPercent: 90 })
    }).then(async r => { if (!r.ok) throw new Error(await r.text()); return r.json(); }), 'Tank created.');
  }

  const navItems = ['Overview', 'Live monitoring', 'Leak detection', 'Tank prediction', 'Water savings'];

  return <div className="app-shell">
    <aside className="sidebar">
      <div className="brand"><div className="brand-mark"><Droplets size={23}/></div><div><strong>AquaGuard<span> AI</span></strong><small>WATER INTELLIGENCE</small></div></div>
      <div className="workspace-label">WORKSPACE</div>
      <nav>{navItems.map((item, i) => <button key={item} className={`nav-item ${activePage === item ? 'active' : ''}`} onClick={() => setActivePage(item)}>
        {i === 0 ? <Gauge size={18}/> : i === 1 ? <Waves size={18}/> : i === 2 ? <AlertTriangle size={18}/> : i === 3 ? <Activity size={18}/> : <Leaf size={18}/>}
        {item}
      </button>)}</nav>
      <div className="sidebar-bottom"><div className="connection"><span className="pulse-dot"></span><div><b>Demo environment</b><small>{running ? 'Simulation running' : 'Simulation stopped'}</small></div></div><p>Designed to prevent water waste, one reading at a time.</p></div>
    </aside>

    <main className="main-content">
      <header className="topbar"><div><p className="eyebrow">WATER MONITORING PLATFORM</p><h1>{activePage}</h1></div>
        <div className="top-actions"><span className="source-pill"><Database size={14}/> Simulated data</span><button className="icon-button" title="Refresh data" onClick={load}><RefreshCw size={17}/></button><button className="primary-button" disabled={busy} onClick={() => action(running ? api.stopSimulation : api.startSimulation, running ? 'Simulation stopped.' : 'Simulation started.')}>{running ? <Square size={16}/> : <Play size={16}/>} {running ? 'Stop simulation' : 'Start simulation'}</button></div>
      </header>

      {error && <div className="alert-banner error"><AlertTriangle size={18}/><span>{error}</span><button onClick={() => setError('')}>×</button></div>}
      {notice && <div className="alert-banner success"><CheckCircle2 size={18}/><span>{notice}</span><button onClick={() => setNotice('')}>×</button></div>}

      <section className="welcome-panel"><div><span className="welcome-tag"><span className="pulse-dot"></span> SYSTEM {running ? 'ACTIVE' : 'READY'}</span><h2>Every drop tells a story.</h2><p>Monitor water flow, spot unusual patterns, and turn early warnings into meaningful water savings.</p></div><div className="water-art"><Droplets size={84} strokeWidth={1.1}/></div></section>

      <section className="metrics-grid">
        <Metric icon={Waves} label="Current water flow" value={`${formatNumber(dashboard?.currentFlowLpm)} L/min`} note="Latest stored reading" tone="blue"/>
        <Metric icon={AlertTriangle} label="Suspected leaks" value={dashboard?.activeLeaks ?? leaks.filter(x => !['REPAIRED','CLOSED'].includes(x.status)).length} note="Requires review" tone="orange"/>
        <Metric icon={Gauge} label="Tanks at risk" value={dashboard?.tanksAtRisk ?? '—'} note="Based on current flow" tone="purple"/>
        <Metric icon={Leaf} label="Estimated water saved" value={`${formatNumber(dashboard?.estimatedLitersSaved ?? savings?.cumulativeLitersSaved, 0)} L`} note="Estimate, not verified" tone="green"/>
      </section>

      {(activePage === 'Overview' || activePage === 'Live monitoring') && <section className="content-grid">
        <article className="panel chart-panel"><div className="panel-heading"><div><h3>Water flow activity</h3><p>Recent recorded flow readings</p></div><span className="live-label"><span className="pulse-dot"></span> LIVE UPDATES</span></div>
          <div className="chart-wrap"><ResponsiveContainer width="100%" height="100%"><AreaChart data={chartData}><defs><linearGradient id="flowFill" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#0891b2" stopOpacity={0.25}/><stop offset="95%" stopColor="#0891b2" stopOpacity={0.01}/></linearGradient></defs><CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e8eef3"/><XAxis dataKey="label" tickLine={false} axisLine={false} tick={{fontSize:11,fill:'#8191a2'}}/><YAxis tickLine={false} axisLine={false} tick={{fontSize:11,fill:'#8191a2'}}/><Tooltip/><Area type="monotone" dataKey="flowRateLpm" name="Flow (L/min)" stroke="#0891b2" fill="url(#flowFill)" strokeWidth={2.5} /></AreaChart></ResponsiveContainer></div>
          <div className="chart-footer"><span><span className="legend-dot"></span> Water flow (L/min)</span><span>{readings.length} stored readings</span></div>
        </article>
        <article className="panel"><div className="panel-heading"><div><h3>Tank status</h3><p>Current fill percentage</p></div><span className="panel-icon"><Droplets size={18}/></span></div>
          {tankChartData.length ? <div className="tank-chart"><ResponsiveContainer width="100%" height="100%"><BarChart data={tankChartData} layout="vertical" margin={{left:4,right:16}}><CartesianGrid strokeDasharray="3 3" horizontal={false} stroke="#e8eef3"/><XAxis type="number" domain={[0,100]} unit="%" tick={{fontSize:11}}/><YAxis type="category" dataKey="name" width={75} tick={{fontSize:11}}/><Tooltip/><Bar dataKey="fillPercent" name="Fill %" fill="#0e7490" radius={[0,5,5,0]}/></BarChart></ResponsiveContainer></div> : <div className="empty-state"><Droplets size={27}/><b>No tanks configured</b><span>Add a tank below to see its fill status.</span></div>}
          <form className="mini-form" onSubmit={createDemoTank}><div className="form-title">Add demo tank</div><div className="form-row"><label>Capacity (L)<input value={tankCapacity} onChange={e=>setTankCapacity(e.target.value)} type="number" min="1" required/></label><label>Current volume (L)<input value={tankVolume} onChange={e=>setTankVolume(e.target.value)} type="number" min="0" required/></label></div><button className="secondary-button" disabled={busy} type="submit">Add tank</button></form>
        </article>
      </section>}

      {(activePage === 'Overview' || activePage === 'Live monitoring') && <section className="content-grid lower-grid">
        <article className="panel"><div className="panel-heading"><div><h3>Submit a manual reading</h3><p>Useful when demonstrating without hardware</p></div><span className="panel-icon"><Wifi size={18}/></span></div>
          <form className="reading-form" onSubmit={submitReading}><label>Monitoring zone<input value={zone} onChange={e=>setZone(e.target.value)} required maxLength="80"/></label><label>Flow rate (L/min)<input type="number" min="0" step="0.1" value={flow} onChange={e=>setFlow(e.target.value)} required/></label><button className="primary-button" disabled={busy} type="submit">Save reading</button></form>
          <button className="secondary-button full-button" disabled={busy} onClick={() => action(api.detectLeaks, 'Leak detection check completed.')}>Run leak detection</button>
        </article>
        <article className="panel"><div className="panel-heading"><div><h3>Recent readings</h3><p>Latest records from the backend</p></div><span className="panel-icon"><Activity size={18}/></span></div>
          <div className="reading-list">{readings.slice(-5).reverse().map(r=><div className="reading-row" key={r.id}><div className="reading-bullet"><Droplets size={15}/></div><div className="reading-info"><b>{r.zone || 'Monitoring zone'}</b><small>{r.timestamp ? new Date(r.timestamp).toLocaleString() : 'Timestamp unavailable'} · {r.source || 'UNKNOWN'}</small></div><strong>{formatNumber(r.flowRateLpm)} <small>L/min</small></strong></div>)}
          {!readings.length && <div className="empty-state compact"><CircleHelp size={24}/><span>No readings yet. Start the simulation or submit one manually.</span></div>}</div>
        </article>
      </section>}

      {activePage === 'Leak detection' && <section className="panel page-panel"><div className="panel-heading"><div><h3>Leak events</h3><p>Suspected events generated from readings</p></div><button className="secondary-button" onClick={() => action(api.detectLeaks, 'Detection run completed.')}>Run detection</button></div>
        {leaks.length ? <div className="table-scroll"><table><thead><tr><th>Zone</th><th>Observed flow</th><th>Risk / status</th><th>Detected</th><th>Action</th></tr></thead><tbody>{leaks.map(l=><tr key={l.id}><td>{l.zone || '—'}</td><td>{formatNumber(l.flowRateLpm)} L/min</td><td><span className={`status-chip ${String(l.status).toLowerCase()}`}>{l.status || 'SUSPECTED'}</span></td><td>{l.detectedAt ? new Date(l.detectedAt).toLocaleString() : '—'}</td><td>{l.status !== 'REPAIRED' && <button className="table-button" onClick={() => action(() => api.repairLeak(l.id, Math.max(0, Number(l.flowRateLpm || 0) * 0.2)), 'Leak repair recorded.')}>Mark repaired</button>}</td></tr>)}</tbody></table></div> : <div className="empty-state"><CheckCircle2 size={28}/><b>No leak events available</b><span>Submit a high-flow reading and run detection.</span></div>}
      </section>}

      {activePage === 'Tank prediction' && <section className="panel page-panel"><div className="panel-heading"><div><h3>Tank overflow prediction</h3><p>Predictions use current stored tank settings and constant-flow assumptions.</p></div></div>
        {tanks.length ? tanks.map(t=><TankCard key={t.id} tank={t}/>) : <div className="empty-state"><Droplets size={28}/><b>No tanks configured</b><span>Create a demo tank on the Overview page.</span></div>}
      </section>}

      {activePage === 'Water savings' && <section className="panel page-panel"><div className="panel-heading"><div><h3>Water savings analytics</h3><p>Calculated from repair records and assumptions.</p></div></div><div className="savings-highlight"><Leaf size={30}/><div><span>Cumulative estimated water saved</span><strong>{formatNumber(savings?.cumulativeLitersSaved, 0)} L</strong><small>Model-estimated, not independently verified</small></div></div><div className="savings-grid"><div><span>Estimated today</span><b>{formatNumber(savings?.todayLitersSaved, 0)} L</b></div><div><span>Estimated monthly</span><b>{formatNumber(savings?.monthlyLitersSaved, 0)} L</b></div><div><span>Repaired events</span><b>{savings?.repairedLeaks ?? leaks.filter(x=>x.status==='REPAIRED').length}</b></div></div></section>}

      <footer className="footer"><span>© 2026 AquaGuard AI</span><span><span className="pulse-dot"></span> Demo mode · simulated readings</span></footer>
    </main>
  </div>;
}

function TankCard({ tank }) {
  const [prediction, setPrediction] = useState(null);
  const [error, setError] = useState('');
  useEffect(() => { api.tankPrediction(tank.id).then(setPrediction).catch(e=>setError(e.message)); }, [tank.id]);
  return <div className="tank-prediction-card"><div><b>{tank.name || `Tank ${tank.id}`}</b><p>{formatNumber(tank.currentVolumeL,0)} / {formatNumber(tank.capacityL,0)} liters</p></div><div className="tank-prediction-stats"><span>Fill level <b>{formatNumber(prediction?.fillPercent ?? (tank.currentVolumeL / tank.capacityL * 100))}%</b></span><span>Time to full <b>{prediction?.minutesToFull == null ? 'Not predicted' : `${formatNumber(prediction.minutesToFull)} min`}</b></span><span>Risk <b>{prediction?.risk || (error ? 'Unavailable' : 'Calculating…')}</b></span></div>{error && <small className="muted">{error}</small>}</div>;
}
