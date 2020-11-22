
namespace CodeInGame
{
    using System.IO;
    using System.Text;

    public interface ILogger
    {
        void Log(string line);
    }

    public interface IWriter
    {
        void WriteLine(string line);
    }

    public interface IReader
    {
        string ReadLine();
    }

    public class Communicator : ILogger, IWriter, IReader
    {
        private readonly StringBuilder history = new StringBuilder();
        private readonly TextWriter writer;
        private readonly TextReader reader;
        private readonly TextWriter logger;

        public Communicator(TextWriter writer, TextReader reader, TextWriter logger)
        {
            this.writer = writer;
            this.reader = reader;
            this.logger = logger;
        }

        public string ReadLine()
        {
            var ret = reader.ReadLine();
            this.history.AppendLine(ret);
            return ret;
        }

        public string FlushHistory()
        {
            var ret = history.ToString();
            history.Clear();
            return ret;
        }

        public void WriteLine(string line)
        {
            this.writer.WriteLine(line);
        }

        public void Log(string line)
        {
            this.logger.WriteLine(line);
        }
    }
}

namespace Decision.Genetics
{
    using System;

    public class BoolGene : DiscretGene<bool>
    {

        public BoolGene() : base(true, false)
        {
        }

        public override IGene Clone()
        {
            return new BoolGene()
            {
                Value = Value
            };
        }

        public override void Load(string v)
        {
            Value = Boolean.Parse(v);
        }

        protected override DiscretGene<bool> Fork(bool v)
        {
            return new BoolGene()
            {
                Value = v
            };
        }
    }
}

namespace Decision.Genetics
{
    using System;
    using System.Collections.Generic;
    using System.Globalization;
    using System.Linq;

    public abstract partial class DiscretArrayGene<T> : IGene, IGeneValue<T[]> where T : struct
    {
        protected static readonly Random Rand = new Random();
        protected readonly T[] PossibleValues;
        protected int Count => Value.Length;

        public double UniqueCount() => (int)Math.Pow(this.PossibleValues.Length, this.Value.Length);

        public IEnumerable<IGene> Uniques()
        {
            foreach (var item in PossibleValues.CartesianRepeat(Value.Length))
            {
                yield return Fork(item.ToArray());
            }
        }

        protected abstract DiscretArrayGene<T> Fork(T[] data);

        public T[] Value { get; protected set; }

        public DiscretArrayGene(int count, params T[] possibleValues)
        {
            this.PossibleValues = possibleValues;
            this.Value = new T[count];
        }

        public DiscretArrayGene(T[] values, params T[] possibleValues)
        {
            this.PossibleValues = possibleValues;
            this.Value = values;
        }

        public void SetGeneRandom()
        {
            for (int i = 0; i < Value.Length; i++)
            {
                this.Value[i] = PossibleValues.OneRandomly();
            }
        }

        public void Mutate()
        {
            var i = Rand.Next(0, Value.Length);
            var v = PossibleValues.AnotherOneRandomly(this.Value[i]);
            this.Value[i] = v;
        }

        public abstract IGene Clone();


        public override string ToString()
        {
            return string.Join(',', Value);
        }

        public void Load(string v)
        {
            var values = v.Split(',');
            for (int i = 0; i < values.Length; i++)
            {
                Value[i] = ParseValue(values[i]);
            }
        }

        protected abstract T ParseValue(string v);

        public IEnumerable<ITranferable> Transferables()
        {
            for (int i = 0; i < Value.Length; i++)
            {
                var stick = i;
                yield return new DelegateTransferable((x) => { Value[stick] = (T)x; }, () => Value[stick]);
            }
        }

        public int TransferableCount()
        {
            return Count;
        }
    }
}

namespace Decision.Genetics
{
    using System;
    using System.Collections.Generic;
    using System.Globalization;
    using System.Linq;

    public abstract class DiscretGene<T> : IGene, ITranferable, IGeneValue<T> where T : struct
    {

        protected readonly T[] PossibleValues;
        public T Value { get; protected set; }

        public DiscretGene(params T[] possibleValues)
        {
            this.PossibleValues = possibleValues;
        }

        public void SetGeneRandom()
        {
            this.Value = PossibleValues.OneRandomly();
        }

        public void Mutate()
        {
            this.Value = PossibleValues.AnotherOneRandomly(this.Value);
        }

        public void Crossover(object o)
        {
            var g = ((DiscretGene<T>)o);
            var v = Value;
            Value = g.Value;
            g.Value = v;
        }

        public abstract IGene Clone();


        public override string ToString()
        {
            return Value.ToString();
        }

        public abstract void Load(string v);

        public double UniqueCount() => this.PossibleValues.Length;

        public IEnumerable<ITranferable> Transferables()
        {
            yield return this;
        }

        public int TransferableCount()
        {
            return 1;
        }

        public IEnumerable<IGene> Uniques()
        {
            foreach(var v in PossibleValues){
                 yield return Fork(v);
            }
        }

        protected abstract DiscretGene<T> Fork(T v);
    }
}

namespace Decision.Genetics
{
    using System.Globalization;

    public class FloatDiscretGene : DiscretGene<float>
    {
        public FloatDiscretGene(params float[] possibleValues) : base(possibleValues)
        {
        }

        public override IGene Clone()
        {
            return new FloatDiscretGene(PossibleValues)
            {
                Value = Value
            };
        }

        public override void Load(string v)
        {
            Value = float.Parse(v);
        }

        protected override DiscretGene<float> Fork(float v)
        {
            return new FloatDiscretGene(PossibleValues){
                Value = v
            };
        }
    }
}

namespace Decision.Genetics
{

    using System.Linq;

    public class IntegerArrayGene : DiscretArrayGene<int>
    {
        public IntegerArrayGene(int count, params int[] possibleValues) : base(count, possibleValues)
        {

        }

        public IntegerArrayGene(int[] values, params int[] possibleValues) : base(values, possibleValues)
        {
        }

        public override IGene Clone()
        {
            return new IntegerArrayGene(Count, PossibleValues)
            {
                Value = this.Value.ToArray()
            };
        }

        protected override DiscretArrayGene<int> Fork(int[] data)
        {
            return new IntegerArrayGene(data, PossibleValues);
        }

        protected override int ParseValue(string v)
        {
            return int.Parse(v);
        }
    }
}

namespace Decision.Genetics
{
  using System.Collections.Generic;
  public interface ITrainer
  {

    List<Genome> Train(Selection selection, int iterations);
  }


  public interface ISort
  {
    Session Train(List<Genome> population);
  }
}
namespace Decision.Genetics
{

  public class Match
    {
      public Player APlayer;
      public Player BPlayer;

      public Score Score;
    }

}
namespace Decision.Genetics
{
  public class Player
    {
      public Genome Genome;
      public float Rating;
    }

}


namespace Decision.Genetics
{
  using System.Collections.Generic;

  public class Session
    {
      public List<Player> Board;
      public int MatchesCount;
    }

}




namespace Decision.Genetics
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    public class BubbleSort : ISort
    {
        private readonly Func<Genome, Genome, Score> MatchFunc;

        public BubbleSort(Func<Genome, Genome, Score> MatchFunc)
        {
            this.MatchFunc = MatchFunc;
        }

        public Session Train(List<Genome> population)
        {
            var matches = 0;
            var player = population.Select(x => new Player { Genome = x, Rating = 0f }).ToList();
            var swapped = 0;
            var n = player.Count;
            do
            {

                swapped = 0;
                for (int i = 1; i < n; ++i)
                {
                    var a = player.ElementAt(i - 1);
                    var b = player.ElementAt(i);
                    var r = MatchFunc(a.Genome, b.Genome);
                    if (r.AScore < r.BScore)
                    {
                        player[i - 1] = b;
                        player[i] = a;
                        swapped += 1;
                    }
                    matches += 1;
                }
                //Console.Out.WriteLine($"**********************************************");
                Console.Out.WriteLine($"SortByBubbleSort swaps={swapped}...");
                //Console.Out.WriteLine($"**********************************************");
                // for (int i = player.Count - 1; i >= 0; --i)
                // {
                //     var g = player[i];
                //     g.Rating = i;
                //     Console.Out.WriteLine($"[G{g.Genome.Generation} N{g.Genome.Number} S{Math.Round(g.Rating)}]{g.Genome.Id()}");
                // }

                n -= 1;

            } while (swapped > 0);

            for (int i = 1; i < player.Count; ++i)
            {
                player[i].Rating = player.Count - i;
            }

            return new Session
            {
                Board = player,
                MatchesCount = matches
            };
        }
    }

}

namespace Decision.Genetics
{


using System;
using System.Collections.Generic;
using System.Linq;

  public class ChampionshipSort : ISort
  {


    private readonly Func<Genome, Genome, Score> MatchFunc;

    public ChampionshipSort(Func<Genome, Genome, Score> MatchFunc)
    {
      this.MatchFunc = MatchFunc;
    }

    public Session Train(List<Genome> population)
    {
      var matches = 0;
      var player = population.Select(x => new Player { Genome = x, Rating = 0f }).ToList();
      var results = Selection.Matches(player).ToList().AsParallel().Select((m) =>
      {
        var score = MatchFunc(m.Item1.Genome, m.Item2.Genome);

        if (false)
        {
          var scoreCheck = MatchFunc(m.Item2.Genome, m.Item1.Genome);
          if (scoreCheck.AScore != score.BScore || scoreCheck.BScore != score.AScore)
          {
            Console.Out.WriteLine($"Asymmetric MatchFunc!");
            Console.Out.WriteLine($"{m.Item1.Genome.Id()}");
            Console.Out.WriteLine($"{m.Item2.Genome.Id()}");

            MatchFunc(m.Item1.Genome, m.Item2.Genome);
            MatchFunc(m.Item2.Genome, m.Item1.Genome);
          }
        }
        return new Match { Score = score, APlayer = m.Item1, BPlayer = m.Item2 };
      }).ToList();

      foreach (var match in results)
      {
        //Console.Out.WriteLine($"[MATCH] N{match.APlayer.Genome.Number}={match.Score.AScore} VS N{match.BPlayer.Genome.Number}={match.Score.BScore}");

        match.APlayer.Rating += match.Score.AScore;
        match.BPlayer.Rating += match.Score.BScore;
      }

      matches = results.Count();


      var sorted = player.OrderByDescending(x => x.Rating);
      return new Session
      {
        Board = sorted.ToList(),
        MatchesCount = matches
      };
    }
  }

}



namespace Decision.Genetics
{

    using System;
    using System.Collections.Generic;
    using System.Linq;
    public class EloSort : ISort
    {
        private static Random Rand = new Random();
        private readonly Func<Genome, Genome, Score> MatchFunc;
        private readonly int nbMatches;
        private readonly float ELO_K;

        public EloSort(Func<Genome, Genome, Score> MatchFunc, int nbMatches, float ELO_K = 30f)
        {
            this.MatchFunc = MatchFunc;
            this.nbMatches = nbMatches;
            this.ELO_K = ELO_K;
        }

        public Session Train(List<Genome> population)
        {
            var matches = 0;
            var players = population.Select(x => new Player { Genome = x, Rating = 400f }).OrderBy(x => Rand.Next()).ToList();

            foreach (var i in Enumerable.Range(0, nbMatches))
            {
                foreach (var n in Enumerable.Range(1, population.Count() - 1))
                {
                    var P1 = players.ElementAt(n - 1);
                    var P2 = players.ElementAt(n);

                    var total = P1.Rating + P2.Rating;
                    var P1Excepted = (1f / (1f + MathF.Pow(10f, ((P1.Rating - P2.Rating) / 400f))));
                    var P2Excepted = (1f / (1f + MathF.Pow(10f, ((P2.Rating - P1.Rating) / 400f))));
                    var score = MatchFunc(P1.Genome, P2.Genome);
                    P1.Rating += (score.AScore - P1Excepted) * ELO_K;
                    P2.Rating += (score.BScore - P2Excepted) * ELO_K;
                    matches += 1;
                }
                players = players.OrderByDescending(x => x.Rating).ToList();
            }

            return new Session
            {
                Board = players,
                MatchesCount = matches
            };
        }
    }

}

namespace Decision.Genetics
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.Linq;
    using System.Threading.Tasks;

    public class GenomeEmptyException : InvalidOperationException { }

    public class Selection
    {
        private readonly int SIZE;
        private readonly int RETAIN;
        private readonly int RandomPop;
        private readonly float RANDOM_SELECTION;
        private readonly float RANDOM_MUTATION;

        private int GenerationIndex = 0;
        private readonly Genome Seed;

        public List<Genome> Population { get; private set; }

        private readonly Random Rand = new Random();

        private HashSet<string> History = new HashSet<String>();

        public Selection(Genome seed, int size, int retain, int randomPop, float randomSelection, float randomMutation)
        {
            if (size < 2) throw new ArgumentException("Excepted size >= 2");
            if (size % 2 != 0) throw new ArgumentException("Excepted size % 2 == 0");
            if (retain <= 2 && retain >= size) throw new ArgumentException("Excepted 2 < retain < size");
            if (seed.Count == 0) throw new ArgumentException("Excepted Seed.Count > 0");
            if (randomSelection < 0f || randomSelection > 1f) throw new ArgumentException("Excepted randomSelection = [0,1]");
            if (randomMutation < 0f || randomMutation > 1f) throw new ArgumentException("Excepted randomSelection = [0,1]");

            this.SIZE = size;
            this.RETAIN = retain;
            this.RandomPop = randomPop;
            this.RANDOM_SELECTION = randomSelection;
            this.RANDOM_MUTATION = randomMutation;
            this.Seed = seed;

            this.Population = Seed.Population(SIZE).Where(x => History.Add(x.Id())).ToList();
        }
        public Genome RegisterGenome(Genome g)
        {
            if (g.UniqueCount() == History.Count())
            {
                throw new GenomeEmptyException();
            }
            var count = 0;
            while (History.Contains(g.Id()))
            {
                g.MutateOne();

                if (++count > 1000) {
                    throw new GenomeEmptyException();
                }
            }
            History.Add(g.Id());
            return g;
        }

        public Genome MutateGenome(Genome g)
        {
            var ret = g.Clone(GenerationIndex);
            ret.MutateOne();
            return RegisterGenome(ret);
        }

        public Tuple<Genome, Genome> Breed(Genome a, Genome b)
        {
            var childA = a.Clone(GenerationIndex);
            var childB = b.Clone(GenerationIndex);

            if (RANDOM_MUTATION > Rand.Next()) childA.MutateOne();
            if (RANDOM_MUTATION > Rand.Next()) childB.MutateOne();

            var exchanges = Rand.Next(1, childA.TransferableCount() - 1);
            var tA = childA.Transferables().ToList();
            var tB = childA.Transferables().ToList();
            foreach (var i in Enumerable.Range(0, childA.TransferableCount() - 1))
            {
                if (i < exchanges)
                {
                    tA[i].Crossover(tB[i]);
                }
            }
            return new Tuple<Genome, Genome>(RegisterGenome(childA), RegisterGenome(childB));
        }

        public static IEnumerable<Tuple<Player, Player>> Matches(List<Player> players)
        {
            for (int i = 0; i < players.Count; ++i)
            {
                for (int j = i + 1; j < players.Count; ++j)
                {
                    yield return new Tuple<Player, Player>(players[i], players[j]);
                }
            }
        }

        public void LastGeneration(Session session)
        {
            Population = session.Board.Select(x => x.Genome).ToList();
            var retain = Math.Min(RETAIN, Population.Count);
            var nextGen = session.Board.GetRange(0, retain).ToList();
            var loosers = session.Board.GetRange(retain, session.Board.Count - retain).ToList();
            PrintSession(session, nextGen, loosers);
        }

        public void NextGeneration(Session session)
        {
            GenerationIndex += 1;
            var population = session.Board;
            var max = population.Max(x => x.Rating);
            var retain = Math.Min(RETAIN, population.Count);

            var nextGen = population.GetRange(0, retain).ToList();
            var loosers = population.GetRange(retain, population.Count - retain).ToList();
            // var nextGen = population.Where(x => x.Rating == max);
            // var loosers = population.Where(x => x.Rating != max);
            PrintSession(session, nextGen, loosers);

            var ret = nextGen.Select(x => x.Genome).ToList();
            foreach (var looser in loosers)
            {
                if (RANDOM_SELECTION > Rand.NextDouble())
                {
                    ret.Add(MutateGenome(looser.Genome));
                }
            }

            var parents = nextGen.Select(x => x.Genome);

            foreach (var i in Enumerable.Range(0, (int)Math.Round((SIZE - ret.Count) / 2f)- RandomPop ))
            {
                var couple = parents.OrderBy(x => Rand.Next()).Take(2);
                var babies = Breed(couple.ElementAt(0), couple.ElementAt(1));
                ret.Add(babies.Item1);
                ret.Add(babies.Item2);
            }
            foreach(var i in Enumerable.Range(0, RandomPop)){
                var pop = Seed.Clone(GenerationIndex);
                pop.SetGenesRandom();
                pop = RegisterGenome(pop);
                ret.Add(pop);
            }

            Console.Out.WriteLine($"New population: {ret.Count()}");
            Population = ret;
        }

        private void PrintSession(Session session, List<Player> nextGen, List<Player> loosers)
        {
            Console.Out.WriteLine($"##############################################");
            Console.Out.WriteLine($"# BOARD  (Matches:{session.MatchesCount}, gen:{GenerationIndex}, coverage:{History.Count}/{Seed.UniqueCount()})");
            Console.Out.WriteLine($"##############################################");

            foreach (var g in nextGen)
            {
                Console.Out.WriteLine($"[G{g.Genome.Generation} N{g.Genome.Number} S{Math.Round(g.Rating)}]{g.Genome.Id()}");
            }

            // Console.Out.WriteLine($"------------------------------------------------------------------------------------------------------------");
            // foreach (var g in loosers)
            // {
            //     Console.Out.WriteLine($"[G{g.Genome.Generation} N{g.Genome.Number} S{Math.Round(g.Rating)}]{g.Genome.Id()}");
            // }
        }
    }
}




namespace Decision.Genetics
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.Linq;
    public class Trainer1VS1 : ITrainer
    {
        private readonly ISort Sort;


        public Trainer1VS1(ISort sort)
        {
            this.Sort = sort;
        }

        public List<Genome> Train(Selection selection, int iterations)
        {

            foreach (var g in Enumerable.Range(1, iterations))
            {
                Session session = TrainOnce(selection.Population);
                try
                {
                    selection.NextGeneration(session);
                }
                catch (GenomeEmptyException)
                {
                    Console.Out.WriteLine($"Selection has generated all combinaisons.");
                    if (selection.Population.Count() > 2)
                    {
                        TrainOnce(selection.Population);
                        selection.LastGeneration(session);
                    } // train last one
                    break;
                }
            }

            Console.Out.WriteLine($"##############################################");
            Console.Out.WriteLine($"Best: {selection.Population.First()}");

            return selection.Population;
        }

        private Session TrainOnce(List<Genome> population)
        {
            var sw = Stopwatch.StartNew();
            var session = Sort.Train(population);
            sw.Stop();
            Console.Out.WriteLine($"Time: {sw.Elapsed}");
            return session;
        }

        private Session PlayTheBoss(Session session, Func<Genome, Score> MatchFunc)
        {
            var BossScore = 0f;
            foreach (var p in session.Board)
            {
                var s = MatchFunc(p.Genome);
                p.Rating += s.AScore;
                BossScore += s.BScore;
                session.MatchesCount += 1;
            }

            Console.Out.WriteLine($"The Boss : S{BossScore}");
            session.Board.OrderByDescending(x => x.Rating);
            return session;
        }
    }
}


namespace Decision.Genetics
{


  using System;
  using System.Collections.Generic;
  using System.Linq;

  public static class ArrayIndexerHelper
  {

    public static void Smooth(this ArrayIndexer<int> self, double k = 4)
    {
      var m = self.Clone();
      foreach (var c in self.AllCoords())
      {
        var Neighbor = self.PlusNeighbor(c).Where(x => self.IsValidCoord(x));
        var sum = Neighbor.Sum(x => m.Get(x));
        var v = m.Get(c);
        self.Set(self.Index(c), (int)Math.Round((sum + v * k) / (k + Neighbor.Count())));
      }
    }

    public static ArrayIndexer<int> DoubleResolution(this ArrayIndexer<int> self)
    {
      var newDim = self.Dimensions.Select(x => x * 2).ToArray();
      var count = newDim.CartesianProduct();
      var ret = new ArrayIndexer<int>(new int[count], newDim);

      foreach (var c in ret.AllCoords())
      {
        var lowRes = c.Select(x => (int)MathF.Floor(x / 2f)).ToArray();
        var v = self.Get(lowRes);
        var index = ret.Index(c);
        ret.Set(index, v);
      }
      return ret;
    }
  }

  public class ArrayIndexer<T>
  {
    private readonly T[] Data;
    public readonly int[] Dimensions;
    public readonly int Count;

    public ArrayIndexer<T> Clone()
    {
      return new ArrayIndexer<T>(Data.ToArray(), Dimensions);
    }

    public T[] GetData() => Data;

    public ArrayIndexer(T[] data, params int[] dimensions)
    {
      this.Data = data;
      this.Dimensions = dimensions;
      Count = dimensions.CartesianProduct();
    }

    public void Fill(T v)
    {
      for (int i = 0; i < Data.Length; i++)
      {
        Data[i] = v;
      }
    }

    public void Set(int index, T value)
    {
      Data[index] = value;
    }

    public T Get(params int[] indexes)
    {
      var index = Index(indexes);
      index = Index(indexes);
      return Data[index];
    }


    public T Get(int index)
    {
      return Data[index];
    }

    public int Index(params int[] indexes)
    {
      var index = indexes[Dimensions.Length - 1];
      for (int d = 0; d < Dimensions.Length - 1; d++)
      {
        var n = indexes[d];
        for (int f = d + 1; f < Dimensions.Length; f++)
        {
          n *= Dimensions[f];
        }
        index += n;
      }
      return index;
    }


    public bool IsValidCoord(params int[] indexes)
    {

      for (int dim = 0; dim < Dimensions.Length; dim++)
      {
        var max = Dimensions[dim];
        var c = indexes[dim];
        if (c < 0 || c >= max)
        {
          return false;
        }
      }
      return true;
    }


    public IEnumerable<int[]> PlusNeighbor(params int[] indexes)
    {
      var ret = new List<int[]>();
      for (int i = 0; i < indexes.Length; i++)
      {
        var nMinus = indexes.ToArray();
        nMinus[i] -= 1;
        ret.Add(nMinus);

        var nPlus = indexes.ToArray();
        nPlus[i] += 1;
        ret.Add(nPlus);
      }


      return ret;
    }

    public IEnumerable<int[]> CrossNeighbor(params int[] indexes)
    {
      IEnumerable<int[]> ret = new List<int[]>() { indexes };

      for (int i = 0; i < Dimensions.Length; i++)
      {
        ret = _Cross(i, ret);
      }
      return ret.Select(x => x.ToArray());
    }

    private IEnumerable<int[]> _Cross(int position, IEnumerable<int[]> values)
    {
      foreach (var v in values)
      {
        var nextA = v.ToArray();
        nextA[position] -= 1;
        yield return nextA;

        var nextB = v.ToArray();
        nextB[position] += 1;
        yield return nextB;
      }
    }

    public IEnumerable<int[]> AllCoords()
    {
      var ret = Enumerable.Range(0, Dimensions[0]).Select(x => new List<int>() { x });

      foreach (var dim in Dimensions.Skip(1))
      {
        ret = _AllCoords(dim, ret);
      }
      return ret.Select(x => x.ToArray());
    }

    private IEnumerable<List<int>> _AllCoords(int dim, IEnumerable<List<int>> values)
    {
      foreach (var v in values)
      {
        for (int i = 0; i < dim; i++)
        {
          var next = new List<int>(v);
          next.Add(i);
          yield return next;
        }
      }
    }
  }
}

namespace Decision.Genetics
{

    using System;

    public static class ArrayIndexerSample
    {

        public static void Sample()
        {
            var DIMX = 10;
            var DIMY = 10;
            var indexer = new ArrayIndexer<int>(new int[DIMX * DIMY], DIMX, DIMY);
            indexer.Fill(0);

            indexer.Set(indexer.Index(0, 0), 10);
            indexer.Set(indexer.Index(9, 9), 10);
            indexer.Set(indexer.Index(5, 5), 10);
            indexer.Smooth();
            for (int x = 0; x < 10; x++)
            {
                var row = "";
                for (int y = 0; y < 10; y++)
                {
                    var v = indexer.Get(x, y);
                    row += v.ToString().PadLeft(3, ' ') + ",";
                }
                Console.WriteLine(row);
            }

            foreach (var c in indexer.AllCoords())
            {
                var index = indexer.Index(c);
                Console.WriteLine($"{string.Join(",", c)} => {index}");
            }

            var doubled = indexer.DoubleResolution();

            for (int x = 0; x < 20; x++)
            {
                var row = "";
                for (int y = 0; y < 20; y++)
                {
                    var v = doubled.Get(x, y);
                    row += v.ToString().PadLeft(3, ' ') + ",";
                }
                Console.WriteLine(row);
            }


        }
    }
}

namespace Decision.Genetics
{
    using System;

    public class DelegateTransferable : ITranferable
    {
        private readonly Action<object> setter;
        private readonly Func<object> getter;

        public DelegateTransferable(Action<object> setter, Func<object> getter)
        {
            this.setter = setter;
            this.getter = getter;
        }

        public void Crossover(object o)
        {
            var g = ((DelegateTransferable)o);
            var v = getter();
            setter(g.getter());
            g.setter(v);
        }
    }
}


namespace Decision.Genetics
{
    using System;
    using System.Collections.Generic;
    using System.Linq;

    public class Dimension
    {
        private readonly float min;
        private readonly float max;
        private readonly float range;
        public readonly int Count;
        private readonly float step;
        private readonly float halfStep;


        // (min, max]
        public Dimension(float min, float max, int count)
        {
            this.min = min;
            this.max = max;
            this.range = max - min;
            this.step = range / (float)count;
            this.halfStep = step * 0.5f;
            this.Count = count;
        }

        public Dimension Double()
        {
            return new Dimension(min, max, Count * 2);
        }

        public int ValueToLabel(float v)
        {
            if (v <= min || v > max)
            {
                throw new ArgumentException($"excepted: v = ({min}, {max}] found: {v}");
            }
            var f = (int)Math.Ceiling((v - min) / step) - 1; // (min, max]
            return f;
        }

        public float LabelToValue(int label)
        {
            if (label < 0 || label >= Count)
            {
                throw new ArgumentException("excepted: label = [0, {count}) found: {label}");
            }
            return min + label * step + halfStep;
        }

        public float ValueToValue(float v)
        {
            return LabelToValue(ValueToLabel(v));
        }

        public IEnumerable<float> Values()
        {
            for (int i = 0; i < this.Count; i++)
            {
                yield return LabelToValue(i);
            }
        }

        public IEnumerable<int> Labels()
        {
            return Enumerable.Range(0, Count);
        }
    }
}


namespace Decision.Genetics
{

    using System;
    using System.Collections.Generic;
    using System.Linq;
    public static class DimensionSample
    {

        public static void Sample()
        {
            var d1 = new Dimension(-100, 100, 2);

            Console.WriteLine($"Values: {string.Join(",", d1.Values())}");
            Console.WriteLine($"Labels-> Values: {string.Join(",", d1.Labels().Select(x => d1.LabelToValue(x)))}");
            Console.WriteLine($"Values-> Values: {string.Join(",", d1.Values().Select(x => d1.ValueToValue(x)))}");


            Console.WriteLine($"Labels: {string.Join(",", d1.Labels())}");
            Console.WriteLine($"Values-> Labels: {string.Join(",", d1.Values().Select(x => d1.ValueToLabel(x)))}");

            Console.WriteLine($"Values*-> Values: {string.Join(",", Enumerable.Range(-99, 200).Select(x => x + "->" + d1.ValueToValue(x)))}");
        }
    }
}

namespace Decision.Genetics
{
    using System;
    using System.Collections.Generic;
    using System.Linq;

    public static class GenomeSample
    {
        public static void Combinations()
        {

            var seed = new Genome().BoolGene("BOOL").IntegerArrayGene("ARRAY", 3, new int[] { 1, 2, 3 });
            var uniques = seed.Uniques();
            foreach (var g in uniques)
            {
                Console.WriteLine(g.Id());
            }
            Console.WriteLine($"COUNT={uniques.Count()}[{seed.UniqueCount()}]");
        }
    }

    public class Genome
    {
        public static int NumberIndex = 0;

        public int Generation = 0;
        public int Number = 0;


        private Dictionary<string, IGene> Genes = new Dictionary<string, IGene>();
        public Genome()
        {

        }

        private Genome(Dictionary<string, IGene> genes)
        {
            this.Genes = genes;
        }

        public double UniqueCount() => Genes.Values.Select(x => x.UniqueCount()).CartesianProduct();

        public IEnumerable<Genome> Uniques()
        {
            var first = Genes.First();
            var ret = first.Value.Uniques().Select(x => new Dictionary<string, IGene>() { [first.Key] = x }).ToList();

            foreach (var g in Genes.Skip(1))
            {
                ret = _Uniques(g, ret).ToList();
            }
            return ret.Select(x => new Genome(x)).ToList();
        }

        private IEnumerable<Dictionary<string, IGene>> _Uniques(KeyValuePair<string, IGene> gene, IEnumerable<Dictionary<string, IGene>> values)
        {
            foreach (var v in values)
            {
                var uniques = gene.Value.Uniques().ToList();
                foreach (var g in uniques)
                {
                    var fork = v.ToDictionary(x => x.Key, x => x.Value.Clone());
                    fork.Add(gene.Key, g);
                    yield return fork;
                }
            }
        }

        public Genome Load(string content)
        {
            var properties = content.Split(";");
            foreach (var p in properties)
            {
                var b = p.Split("=");
                var key = b[0];
                var v = b[1];
                var gene = Genes[key];
                gene.Load(v);
            }

            return this;
        }

        public int Count => Genes.Count;

        public T Get<T>(string name)
        {
            return ((IGeneValue<T>)Genes[name]).Value;
        }

        public int GetInt(string name)
        {
            return ((IGeneValue<int>)Genes[name]).Value;
        }

        public float GetFloat(string name)
        {
            return ((IGeneValue<float>)Genes[name]).Value;
        }

        public string GetString(string name)
        {
            return ((IGeneValue<string>)Genes[name]).Value;
        }

        public int[] GetIntegerArray(string name)
        {
            return ((IGeneValue<int[]>)Genes[name]).Value;
        }


        public ArrayIndexer<int> GetIntegerArrayIndexer(string name, params int[] dimensions)
        {
            var v = GetIntegerArray(name);
            return new ArrayIndexer<int>(v, dimensions);
        }

        public void DoubleIntegerArray(string name, int[] possibleValues, params int[] dimensions)
        {
            var target = ((IntegerArrayGene)Genes[name]);
            var v = new ArrayIndexer<int>(target.Value, dimensions).DoubleResolution().GetData();
            Genes[name] = new IntegerArrayGene(v, possibleValues);
        }


        public bool GetBool(string name)
        {
            return ((IGeneValue<bool>)Genes[name]).Value;
        }




        public string Id()
        {
            return string.Join(";", Genes.Select(x => $"{x.Key}={x.Value.ToString()}"));
        }

        public void SetGenesRandom()
        {
            foreach (var g in Genes.Values)
            {
                g.SetGeneRandom();
            }
        }

        public void MutateOne()
        {
            Genes.Values.OneRandomly().Mutate();
        }

        public int TransferableCount() => Genes.Values.Sum(g => g.TransferableCount());

        public IEnumerable<ITranferable> Transferables()
        {
            foreach (var g in Genes.Values)
            {
                foreach (var t in g.Transferables())
                {
                    yield return t;
                }
            }
        }


        public Genome ContinuousGene(string name, float min, float max, int count)
        {
            return FloatDiscretGene(name, new Dimension(min, max, count).Values().ToArray());
        }
        public Genome FloatDiscretGene(string name, params float[] possibleValues)
        {
            var ret = new FloatDiscretGene(possibleValues);
            ret.SetGeneRandom();
            this.Genes.Add(name, ret);
            return this;
        }

        public Genome IntegerArrayGene(string name, int count, params int[] possibleValues)
        {
            var ret = new IntegerArrayGene(count, possibleValues);
            ret.SetGeneRandom();
            this.Genes.Add(name, ret);
            return this;
        }

        public Genome BoolGene(string name)
        {
            var ret = new BoolGene();
            ret.SetGeneRandom();
            this.Genes.Add(name, ret);
            return this;
        }

        public Genome Clone(int gen)
        {
            return new Genome()
            {
                Genes = Genes.ToDictionary(entry => entry.Key, entry => entry.Value.Clone()),
                Generation = gen,
                Number = ++NumberIndex
            };
        }

        public IEnumerable<Genome> Population(int size)
        {
            foreach (var i in Enumerable.Range(0, size))
            {
                var ret = Clone(0);

                ret.SetGenesRandom();
                yield return ret;
            }
        }

        public override string ToString()
        {
            return Id();
        }
    }
}

namespace Decision.Genetics
{
    using System;
    using System.Collections.Generic;
    using System.Linq;

    public interface IGeneValue<T>
    {
        public T Value { get; }
    }

    public interface IGene
    {
        IEnumerable<ITranferable> Transferables();

        int TransferableCount();
        double UniqueCount();
        IEnumerable<IGene> Uniques();

        void SetGeneRandom();
        void Mutate();

        IGene Clone();

        void Load(string v);
    }

    public interface ITranferable
    {
        void Crossover(object Value);
    }
}

namespace Decision.Genetics
{
    public struct Score
    {
        public float AScore;
        public float BScore;
    }
}


namespace Decision.Genetics
{

    using System;
    using System.Linq;

    public static class SelectionSample
    {
        public static void Sample()
        {
            var seed = new Genome();
            seed.FloatDiscretGene("default", new Dimension(0f, 10f, 10).Values().ToArray())
                .FloatDiscretGene("G1", 1f, 2f, 3f)
                .FloatDiscretGene("G2", 1f, 2f, 3f);

            var selection = new Selection(seed, size: 150, retain: 10, randomPop: 0, randomSelection: 0.1f, randomMutation: 0.8f);
            var trainer = new Trainer1VS1(new ChampionshipSort((a, b) =>
             {
                 var ga = a.GetFloat("default");
                 var gb = b.GetFloat("default");
                 return new Score
                 {
                     AScore = ga > gb ? 1f : 0f,
                     BScore = gb > ga ? 1f : 0f,
                 };
             }));

            var pop = trainer.Train(selection, 10);
        }

        public static void SampleMultiDim()
        {
            var seed = new Genome();
            var DIMX = new Dimension(0, 10, 2);
            var DIMY = new Dimension(0, 10, 2);
            var DIMV = new Dimension(0, 200, 2);

            seed.IntegerArrayGene("multiDim", DIMX.Count * DIMY.Count, DIMV.Labels().ToArray());
            var seedM2 = new Genome();

            double genomeSize = seed.UniqueCount();
            double complexity = Math.Round(genomeSize * Math.Log(genomeSize));

            //
            // solve(complexity = ((((genomeSize * k) / iterations) * (((genomeSize * k) / iterations) + 1)) / 2) * iterations, k);
            //

            // iterations fixe
            // double iterations = 200;
            // double k = (Math.Sqrt(Math.Pow(iterations, 2) + 8 * complexity * iterations) - iterations) / (2 * genomeSize);

            // K fixe
            //double k = 0.01;
            //double iterations = Math.Round(-(Math.Pow(genomeSize, 2) * Math.Pow(k, 2)) / (genomeSize * k - 2 * complexity));

            // double size = Math.Round(genomeSize * k / iterations);
            // double retain = Math.Round(size * k);
            // double matches = Selection.ChampionshipCount((int)size) * iterations;

            //Console.Out.WriteLine($"k={Math.Round(k * 100)}% genomeSize={genomeSize}  iterations={iterations}, size={size}, retain={retain} matches={matches}[~={complexity}]");

            var selection = new Selection(seed, size: 200, retain: 50, randomPop: 0, randomSelection: 0.5f, randomMutation: 0.5f);
            var rand = new Random();

            Func<Dimension, Dimension, Dimension, Func<Genome, Genome, Score>> evaluate = (dimX, dimY, dimV) => (a, b) =>
                  {
                      var ga = a.GetIntegerArrayIndexer("multiDim", dimX.Count, dimY.Count);
                      var gb = b.GetIntegerArrayIndexer("multiDim", dimX.Count, dimY.Count);
                      var AScore = 0;
                      var BScore = 0;

                      for (int x = 1; x < 10; x++)
                      {
                          for (int y = 1; y < 10; y++)
                          {
                              var excepted = dimV.ValueToValue(x * x + y * y);
                              var xLabel = dimX.ValueToLabel(x);
                              var yLabel = dimY.ValueToLabel(y);

                              var A = Math.Abs(excepted - dimV.LabelToValue(ga.Get(xLabel, yLabel)));
                              var B = Math.Abs(excepted - dimV.LabelToValue(gb.Get(xLabel, yLabel)));
                              AScore += A <= B ? 0 : 1;
                              BScore += A >= B ? 0 : 1;
                          }
                      }
                      return new Score
                      {
                          AScore = AScore <= BScore ? 1 : 0,
                          BScore = AScore >= BScore ? 1 : 0
                      };
                  };

            var trainer = new Trainer1VS1(new ChampionshipSort(evaluate(DIMX, DIMY, DIMV)));
            var pop = trainer.Train(selection, 20);

            //run 2
            for (int i = 0; i < 2; i++)
            {
                //pop.ForEach(x => x.GetIntegerArrayIndexer("multiDim", DIMX, DIMY).Smooth(4));
                DIMV = DIMV.Double();
                pop.ForEach(g => g.DoubleIntegerArray("multiDim", DIMV.Labels().ToArray(), DIMX.Count, DIMY.Count));
                DIMX = DIMX.Double(); DIMY = DIMY.Double();
                trainer = new Trainer1VS1(new ChampionshipSort(evaluate(DIMX, DIMY, DIMV)));
                pop = trainer.Train(selection, 200);
            }

            var Perfect = new ArrayIndexer<int>(new int[DIMX.Count * DIMY.Count], DIMX.Count, DIMY.Count);
            for (int x = 0; x < DIMX.Count; x++)
            {
                for (int y = 0; y < DIMY.Count; y++)
                {
                    var xx = DIMX.ValueToValue((x + 1) * (10f / (float)DIMX.Count));
                    var yy = DIMY.ValueToValue((y + 1) * (10f / (float)DIMY.Count));
                    var v = xx * xx + yy * yy;
                    var label = DIMV.ValueToLabel(v);
                    Perfect.Set(Perfect.Index(x, y), label);
                }
            }

            Console.WriteLine("--------------------------------------------------------");

            var data = pop.First().GetIntegerArrayIndexer("multiDim", DIMX.Count, DIMY.Count);

            for (int x = 0; x < DIMX.Count; x++)
            {
                var row = "";
                for (int y = 0; y < DIMY.Count; y++)
                {
                    var v = DIMV.LabelToValue(data.Get(x, y));
                    var p = DIMV.LabelToValue(Perfect.Get(x, y));
                    row += v.ToString().PadLeft(3, ' ') + "[" + p.ToString().PadLeft(3, ' ') + "],";
                }
                Console.WriteLine(row);
            }
        }
    }
}

namespace Decision
{
    using System;

    public class AlphaBeta
    {



        public static int ProcessSimultaneous(NodeTree node, int depthLimit)
        {
            if (depthLimit == 0 || node.IsTerminalNode)
            {
                if (depthLimit > 2) Console.WriteLine($"depthLimit={depthLimit} IsTerminalNode={node.IsTerminalNode}");
                return node.Fitness();
            }
            else
            {
                var ret = int.MinValue;
                foreach (var child in node.Children)
                {
                    ret = Math.Max(ret, ProcessSimultaneous(child, depthLimit - 1));
                }
                return ret;
            }
        }

        public static int Process(NodeTree node, int depthLimit, int alpha, int beta, bool maximizingPlayer)
        {
            if (depthLimit == 0 || node.IsTerminalNode)
            {
                return node.Fitness();
            }
            if (maximizingPlayer)
            {
                var ret = int.MinValue;
                foreach (var child in node.Children)
                {
                    ret = Math.Max(ret, Process(child, depthLimit - 1, alpha, beta, false));
                    alpha = Math.Max(alpha, ret);
                    if (alpha >= beta)
                    {
                        break;// beta cutoff
                    }
                }
                return ret;
            }

            else
            {
                var ret = int.MaxValue;
                foreach (var child in node.Children)
                {
                    ret = Math.Min(ret, Process(child, depthLimit - 1, alpha, beta, true));
                    beta = Math.Min(beta, ret);
                    if (beta <= alpha)
                    {
                        break; //alpha cutoff
                    }
                }
                return ret;
            }
        }
    }
}

namespace Decision
{
    using System.Linq;
    using System.Collections.Generic;
    using CodeInGame;
    using System;

    public class Command
    {
        public string subject;
        public string statement;
        public double score;
    }

    public class CommandBag
    {
        private static Random rand = new Random();
        public static T Winner<T>(Dictionary<T, double> target, T panicValue)
        {
            var total = target.Sum(x => x.Value);
            var rand = CommandBag.rand.NextDouble() * total;
            var count = 0d;

            foreach (var item in target.Where(_ => _.Value > 0))
            {
                count += item.Value;
                if (rand <= count)
                {
                    return item.Key;
                }
            }
            return panicValue;
        }

        private readonly List<Command> commands = new List<Command>();
        private readonly IWriter writer;
        private readonly ILogger logger;

        public CommandBag(IWriter writer, ILogger logger)
        {
            this.writer = writer;
            this.logger = logger;
        }
        public void Add(string subject, double score, string statement)
        {
            if (score > 0)
            {
                this.commands.Add(new Command { subject = subject, score = score, statement = statement });
            }
        }
        public void Apply()
        {
            foreach (var command in commands.OrderByDescending(x => x.score).Take(10))
            {
                logger.Log($"[{command.score}][{command.subject}]{command.statement}");
            }
            var best = this.commands.GroupBy(x => x.score).OrderByDescending(x => x.Key).FirstOrDefault();
            if (best != null)
            {
                writer.WriteLine(best.First().statement);
            }
            else
            {
                writer.WriteLine("ERROR No Statement");
            }
        }
    }
}
namespace Decision
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    public static class EnumerableHelper
    {
        public static Random Rand = new Random();
        public static T OneRandomly<T>(this IEnumerable<T> self)
        {
            var i = Rand.Next(0, self.Count());
            return self.ElementAt(i);
        }
        public static T AnotherOneRandomly<T>(this IEnumerable<T> self, T me) where T : struct
        {
            var choices = self.Where(x => !x.Equals(me)).ToList();
            var index = Rand.Next(0, choices.Count() - 1);
            return choices.ElementAt(index);
        }

        public static double CartesianProduct(this IEnumerable<double> self)
        {
            double count = 1;
            foreach (var f in self)
            {
                count *= f;
            }
            return count;
        }

        public static int CartesianProduct(this IEnumerable<int> self)
        {
            int count = 1;
            foreach (var f in self)
            {
                count *= f;
            }
            return count;
        }

        public static IEnumerable<List<T>> CartesianRepeat<T>(this IEnumerable<T> possibleValues, int m)
        {
            var ret = possibleValues.Select(x => new List<T>() { x });

            foreach (var i in Enumerable.Range(0, m - 1))
            {
                ret = possibleValues.CartesianMulti(ret);
            }
            return ret;
        }

        public static IEnumerable<List<T>> CartesianMulti<T>(this IEnumerable<T> right, IEnumerable<List<T>> left)
        {
            foreach (var v in left)
            {
                foreach (var p in right)
                {
                    var next = new List<T>(v);
                    next.Add(p);
                    yield return next;
                }
            }
        }
    }
}

namespace Drawing
{


    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Linq;
    using System.Numerics;
    using System.Text;

    public class SvgAnimation
    {
        private readonly List<Vector2> Frames = new List<Vector2>();
        private readonly float FrameTime;

        public SvgAnimation(float frameTime)
        {
            this.FrameTime = frameTime;
        }

        public void Add(Vector2 p)
        {
            this.Frames.Add(p);
        }

        private IEnumerable<string> KeyTimes()
        {
            var total = (float)this.Frames.Count();

            for (float i = 0f; i < total; i++)
            {
                yield return F(i/(total-1f));
            }
        }

        public Vector2 StartPosition => this.Frames.First();

        private static string F(float f)
        {
            return MathF.Round(f, 3).ToString(System.Globalization.CultureInfo.InvariantCulture);
        }

        public override string ToString()
        {
            var dur = F(FrameTime*this.Frames.Count());
            var keyTimes = String.Join(';', KeyTimes());
            var xValues = String.Join(';', this.Frames.Select(p => F(p.X)));
            var yValues = String.Join(';', this.Frames.Select(p => F(p.Y)));
            var ret = $"<animate attributeName='cx' dur='{dur}s' repeatCount='indefinite' values='{xValues}' keyTimes='{keyTimes}'/>";
            ret += $"<animate attributeName='cy' dur='{dur}s' repeatCount='indefinite' values='{yValues}' keyTimes='{keyTimes}'/>";
            return ret;
        }
    }
    public class SvgLayer
    {
        static string I(float f)
        {
            return ((int)f).ToString();
        }
        private StringBuilder builder = new StringBuilder();
        public void DrawCircle(Vector2 center, int radius, string color, int width = 1)
        {
            builder.Append($"<circle cx='{I(center.X)}' cy='{I(center.Y)}' r='{radius}' stroke='{color}' stroke-width='{width}' fill='transparent' />");
        }

        public void DrawLabel(Vector2 center, string label)
        {
            builder.Append($"<text class='label' x='{I(center.X)}' y='{I(center.Y)}'>{label}</text>");
        }

        public void DrawAnimation(SvgAnimation animation, int radius, string fill, int width, string stroke)
        {
            var center = animation.StartPosition;
            builder.Append($"<circle cx='{I(center.X)}' cy='{I(center.Y)}' r='{radius}' stroke='{stroke}' fill='{fill}' stroke-width='{width}'>{animation}</circle>");
        }

        public void DrawDisk(Vector2 center, int radius, string color)
        {
            builder.Append($"<circle cx='{I(center.X)}' cy='{I(center.Y)}' r='{radius}' fill='{color}' />");
        }

        public void DrawVector(Vector2 origin, Vector2 vector, string color, int radius = 5, int width = 1)
        {
            var dest = origin + vector;
            this.DrawCircle(dest, radius, color, width);
            this.DrawLine(origin, origin + vector, color, width);
        }

        public void DrawLine(Vector2 begin, Vector2 end, string color, int width = 1)
        {
            builder.Append($"<line x1='{I(begin.X)}' y1='{I(begin.Y)}' x2='{I(end.X)}' y2='{I(end.Y)}' stroke='{color}' stroke-width='{width}' />'");
        }

        public override string ToString()
        {
            return $"<g>{builder.ToString()}</g>";
        }
    }
    public class SvgBuilder
    {
        private readonly int width;
        private readonly int height;
        private readonly List<SvgLayer> layers = new List<SvgLayer>();

        public SvgBuilder(int width, int height)
        {
            this.width = width;
            this.height = height;
        }

        public SvgLayer AddLayer()
        {
            var ret = new SvgLayer();
            this.layers.Add(ret);
            return ret;
        }

        public override string ToString()
        {


            var content = string.Join('\n', layers.Select(x => x.ToString()));
            return $@"<?xml version='1.0' encoding='utf-8'?>
<svg xmlns='http://www.w3.org/2000/svg' version='1.1' width='{width / 5}' height='{height / 5}' viewBox='0 0 {width} {height}'>
    <style type='text/css' >
      <![CDATA[
        text.label {{
            alignment-baseline:middle;
            text-anchor:middle;
            font-size: 30px;
        }}
      ]]>
    </style>
{content}
</svg>";
        }

        public void ToFile(string path)
        {
            using (var f = File.Open(path, FileMode.Create))
            {
                using (var w = new StreamWriter(f))
                {
                    w.Write(this.ToString());
                }
            }
        }

        public string ToDataURI()
        {
            var plainTextBytes = System.Text.Encoding.UTF8.GetBytes(this.ToString());
            var b64 = System.Convert.ToBase64String(plainTextBytes);
            return "data:image/svg+xml;base64," + b64;
        }
    }
}

namespace Stat
{
    using System.Collections.Generic;

    class LifeHistory{

    private List<int> history  = new List<int>();

    public void Add(int v){
        this.history.Add(v);
    }

}
}

namespace Decision
{
    using System.Collections.Generic;

    public interface NodeTree
    {
        IEnumerable<NodeTree> Children { get; }
        bool IsTerminalNode { get; }
        int Fitness();
    }
}

namespace TheGame
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.IO;
    using CodeInGame;
    using Decision.Genetics;
    using Fencing.Models;

    public class MyNode
    {
        public static IEnumerable<MyNode> ProcessAll(MyNode node, int depthLimit)
        {
            if (depthLimit == 0 || node.IsTerminalNode)
            {
                yield return node;
            }
            else
            {
                foreach (var child in node.Children)
                {
                    foreach (var c in ProcessAll(child, depthLimit - 1))
                    {
                        yield return c;
                    }
                }
            }
        }

        public static Dictionary<ActionType, Score> Evaluate(GameModel rootNode, int depth, TimeSpan timeout)
        {
            var match = new MatchModel(new DummyObserver(), rootNode.Clone());
            var root = new MyNode(match, null);
            var ret = new Dictionary<ActionType, Score>();

            var result = ProcessAll(root, depth);
            Stopwatch sw = Stopwatch.StartNew();
            foreach (var r in result)
            {
                var state = r.match.getState();
                var sumA = state.teamA.player.energyMax> 0 ? state.teamA.score : -100;
                var sumB = state.teamA.player.energyMax> 0 ? state.teamB.score : -100;

                if (!ret.ContainsKey(r.Seed))
                {
                    ret.Add(r.Seed, new Score()
                    {
                        AScore = sumA,
                        BScore = sumB
                    });
                }
                else
                {
                    var old = ret[r.Seed];

                    ret[r.Seed] = new Score()
                    {
                        AScore = old.AScore+ sumA,
                        BScore = old.BScore+ sumB,
                    };
                }
                if (sw.Elapsed >= timeout) break;
            }

            return ret;
        }

        public readonly ActionType Seed;

        private readonly MatchModel match;

        MyNode(MatchModel match, ActionType seed)
        {
            this.match = match;
            this.Seed = seed;
        }

        public IEnumerable<MyNode> Children
        {
            get
            {
                var com = new Communicator(new StringWriter(), new StringReader(""), new StringWriter());
                var state = match.getState();
                var obsA = new Observation(state.teamA, state.teamB);
                var obsB = new Observation(state.teamB, state.teamA);
                var actionA = Observation.GetPossibleActions(obsA, obsB);
                var actionB = Observation.GetPossibleActions(obsB, obsA);

                foreach (var a in actionA)
                {
                    foreach (var b in actionB)
                    {
                        var cloned = match.Clone();
                        cloned.tick(a, b);
                        // if(a == ActionType.WALK && cloned.getState().restart){
                        //     Console.Error.WriteLine("restart collision");
                        // }
                        //if (!cloned.theEnd && cloned.getState().restart) { match.restart(); }
                        yield return new MyNode(cloned, Seed ?? a);
                    }
                }
            }
        }

        public bool IsTerminalNode => match.theEnd || match.getState().restart;

        public int Fitness()
        {
            var state = match.getState();
            var a = state.teamA.player.energyMax> 0 ? state.teamA.score: -20;
            var b = state.teamB.player.energyMax> 0 ? state.teamA.score: -20;
            return (a<0) ? a : a - b;
        }
    }
}

namespace TheGame
{
    using System.Collections.Generic;
    using Fencing.Models;
    public class Observation
    {
        public static int LEAGUE = 1;
        public readonly int distance;
        public readonly bool canAttack;
        public readonly bool isLeader;
        public readonly bool isChallenger;
        public readonly bool canWalk;
        public readonly bool canDoubleWalk;
        public readonly bool canRetreat;
        public readonly bool canDoubleRetreat;
        public readonly bool canParry;
        public readonly bool canLunge;

        public readonly bool mustLunge;
        public readonly bool canBreak;
        private readonly bool canConsumeDrug;
        private readonly bool canWalkDrug;
        private readonly bool canDoubleWalkDrug;
        private readonly bool canRetreatDrug;
        private readonly bool canDoubleRetreatDrug;
        private readonly bool canLungeDrug;
        private readonly bool canParryDrug;
        private readonly bool canEnergyMaxDrug;
        private readonly bool canBreakDrug;

        public Observation(TeamModel my, TeamModel your)
        {
            distance = (your.player.position - my.player.position) / ActionType.WALK.move;
            canAttack = 0 <= my.player.energy + distance * ActionType.WALK.energy - ActionType.LUNGE.energy;
            isLeader = my.score > your.score;
            isChallenger = my.score < your.score;
            canWalk = 0 <= my.player.energy + ActionType.WALK.energy;

            canRetreat = distance < 5 && 0 <= my.player.energy + ActionType.RETREAT.energy && my.player.position - ActionType.RETREAT.move >= 0;
            canParry = 0 <= my.player.energy + ActionType.PARRY.energy;
            canLunge = ActionType.PARRY.energyTransfer <= my.player.energy + ActionType.LUNGE.energy && isTouchedWhenAttack(my.player.getRelativePosition(), your.player.getRelativePosition());
            mustLunge = ActionType.PARRY.energyTransfer <= my.player.energy + ActionType.LUNGE.energy && isTouchedWhenAttackDefense(my.player.getRelativePosition(), your.player.getRelativePosition());
            canBreak = my.player.energy < my.player.energyMax;

            if (LEAGUE > 1)
            {
                canDoubleWalk = distance >= 2 && 0 <= my.player.energy + ActionType.DOUBLE_WALK.energy;
                canDoubleRetreat = distance < 5 && 0 <= my.player.energy + ActionType.DOUBLE_RETREAT.energy && my.player.position - ActionType.DOUBLE_RETREAT.move >= 0;
            }

            if (LEAGUE > 2)
            {
                canConsumeDrug = my.player.drugCount <= 7;
                canWalkDrug = canConsumeDrug && 0 <= my.player.energy + ActionType.WALK_DRUG.energy;
                canDoubleWalkDrug = canConsumeDrug && 0 <= my.player.energy + ActionType.DOUBLE_WALK_DRUG.energy;
                canRetreatDrug = canConsumeDrug && 0 <= my.player.energy + ActionType.RETREAT_DRUG.energy;
                canDoubleRetreatDrug = canConsumeDrug && 0 <= my.player.energy + ActionType.DOUBLE_RETREAT_DRUG.energy;
                canLungeDrug = canConsumeDrug && 0 <= my.player.energy + ActionType.LUNGE_DRUG.energy;
                canParryDrug = canConsumeDrug && 0 <= my.player.energy + ActionType.PARRY_DRUG.energy;
                canEnergyMaxDrug = canConsumeDrug && 0 <= my.player.energy + ActionType.ENERGY_MAX_DRUG.energy;
                canBreakDrug = canConsumeDrug && 0 <= my.player.energy + ActionType.BREAK_DRUG.energy;
            }
        }
        public static bool isTouchedWhenAttackDefense(int striker, int defender)
        {
            return (defender + ActionType.PARRY.distance) + (striker + ActionType.LUNGE.distance) >= 500;
        }

        public static bool isTouchedWhenAttack(int striker, int defender)
        {

            return (defender) + (striker + ActionType.LUNGE.distance) >= 500;
        }

        public static List<ActionType> GetPossibleActions(TeamModel me, TeamModel you)
        {
            return GetPossibleActions(new Observation(me, you), new Observation(you, me));
        }
        public static List<ActionType> GetPossibleActions(Observation me, Observation you)
        {
            var actions = new List<ActionType>();
            if (me.canLunge && !you.canParry)
            {
                actions.Add(ActionType.LUNGE);
            }
            else
            {
                if (me.canLunge) actions.Add(ActionType.LUNGE);
                if (me.canBreak) actions.Add(ActionType.BREAK);
                if (me.canWalk) actions.Add(ActionType.WALK);
                if (me.canRetreat && (!you.canLunge && me.distance > 1)) actions.Add(ActionType.RETREAT);
                if (me.canParry && you.canLunge) actions.Add(ActionType.PARRY);
                if (LEAGUE > 1)
                {
                    if (me.canDoubleWalk) actions.Add(ActionType.DOUBLE_WALK);
                    if (me.canDoubleRetreat && (!you.canLunge && me.distance > 0)) actions.Add(ActionType.DOUBLE_RETREAT);
                }
                if (LEAGUE > 2)
                {
                    if (me.canWalkDrug) actions.Add(ActionType.WALK_DRUG);
                    if (me.canDoubleWalkDrug) actions.Add(ActionType.DOUBLE_WALK_DRUG);
                    if (me.canRetreatDrug) actions.Add(ActionType.RETREAT_DRUG);
                    if (me.canDoubleRetreatDrug) actions.Add(ActionType.DOUBLE_RETREAT_DRUG);
                    if (me.canLungeDrug) actions.Add(ActionType.LUNGE_DRUG);
                    if (me.canParryDrug) actions.Add(ActionType.PARRY_DRUG);
                    if (me.canEnergyMaxDrug) actions.Add(ActionType.ENERGY_MAX_DRUG);
                    if (me.canBreakDrug) actions.Add(ActionType.BREAK_DRUG);
                }
            }
            return actions;
        }
    }
}

namespace Fencing.Models
{
    using System;
    using System.Linq;

    public class ActionType
    {
        //League 0
        public static ActionType SUPPRESSED = Action("SUPPRESSED", int.MaxValue, 0, 0, 0, 0);
        public static ActionType BREAK = Action("BREAK", 0, 2, 0, 0, 0);
        public static ActionType WALK = Action("WALK", 0, -1, 20, 0, 0);
        public static ActionType RETREAT = Action("RETREAT", 0, -1, -20, 0, 0);
        public static ActionType LUNGE = Action("LUNGE", 0, -5, 0, 40, 0);
        public static ActionType PARRY = Action("PARRY", 0, -2, 0, -40, 0, 2);

        //League 1
        public static ActionType MIDDLE_POSTURE = Action("MIDDLE_POSTURE", 1, -1, 0, 0, 0);
        public static ActionType TOP_POSTURE = Action("TOP_POSTURE", 1, -1, 0, 0, 0);
        public static ActionType BOTTOM_POSTURE = Action("BOTTOM_POSTURE", 1, -1, 0, 0, 0);
        public static ActionType DOUBLE_WALK = Action("DOUBLE_WALK", 1, -4, 40, 0, 0);
        public static ActionType DOUBLE_RETREAT = Action("DOUBLE_RETREAT", 1, -4, -30, 0, 0);

        //Boss -> TreeExplore algorithm
        // league 2

        public static ActionType LUNGE_DRUG = Action("LUNGE_DRUG", 2, -5, 0, 0, 5);
        public static ActionType PARRY_DRUG = Action("PARRY_DRUG", 2, -5, 0, 0, -5);
        public static ActionType ENERGY_MAX_DRUG = Action("ENERGY_MAX_DRUG", 2, -5, 0, 0, 5);
        public static ActionType WALK_DRUG = Action("WALK_DRUG", 2, -5, 0, 0, 5);
        public static ActionType RETREAT_DRUG = Action("RETREAT_DRUG", 2, -5, 0, 0, 5);
        public static ActionType DOUBLE_WALK_DRUG = Action("DOUBLE_WALK_DRUG", 2, -5, 0, 0, 10);
        public static ActionType DOUBLE_RETREAT_DRUG = Action("DOUBLE_RETREAT_DRUG", 2, -5, 0, 0, 10);
        public static ActionType BREAK_DRUG = Action("BREAK_DRUG", 2, -5, 0, 0, 10);

        public static ActionType[] All = new ActionType[]{
            SUPPRESSED, BREAK, WALK, RETREAT, LUNGE, PARRY,
            MIDDLE_POSTURE, TOP_POSTURE, BOTTOM_POSTURE,DOUBLE_WALK,DOUBLE_RETREAT,
            LUNGE_DRUG,PARRY_DRUG, ENERGY_MAX_DRUG, WALK_DRUG,
            RETREAT_DRUG, DOUBLE_WALK_DRUG, DOUBLE_RETREAT_DRUG,
            BREAK_DRUG
        };

        public readonly string code;
        public readonly int league;
        public readonly int energy;
        public readonly int move;
        public readonly int distance;
        public readonly int drug;
        public readonly int energyTransfer;

        ActionType(string code, int league, int energy, int move, int distance, int drug, int energyTransfer)
        {
            this.code = code;
            this.league = league;
            this.energy = energy;
            this.move = move;
            this.distance = distance;
            this.drug = drug;
            this.energyTransfer = energyTransfer;
        }

        private static ActionType Action(string code, int league, int energy, int move, int distance, int drug, int energyTransfer = 0)
        {
            return new ActionType(code, league, energy, move, distance, drug, energyTransfer);
        }

        public static ActionType fromString(string code)
        {
            return All.FirstOrDefault(x => x.code == code);
        }
    }
}
namespace Fencing.Models
{
    using System;
    using System.Linq;
    public class GameModel
    {
        public int tick = 0;

        public bool restart = false;

        public TeamModel teamA = new TeamModel();

        public TeamModel teamB = new TeamModel();

        public GameModel Clone(){
            return new GameModel(){
                tick = tick,
                restart = restart,
                teamA = teamA.Clone(),
                teamB = teamB.Clone()
            };
        }
    }
}
namespace Fencing.Models
{
    using System;
    using System.Linq;
    public class MatchModel
    {
        public const int MAX_TICK = 400;

        private readonly GameModel state;

        public bool theEnd = false;
        private readonly MatchObserver observer;

        public MatchModel Clone()
        {
            return new MatchModel(observer, state.Clone())
            {
                theEnd = theEnd
            };
        }

        public MatchModel(MatchObserver observer, GameModel state)
        {
            this.observer = observer;
            this.state = state;
        }

        public MatchModel(MatchObserver observer)
        {
            this.observer = observer;
            state = initGame();
        }

        public GameModel getState()
        {
            return state;
        }

        private GameModel initGame()
        {
            GameModel ret = new GameModel();
            ret.tick = 0;
            initTeam(ret.teamA, PlayerModel.SPAWN_POSITION_A, PlayerModel.LEFT_ORIENTATION);
            initTeam(ret.teamB, PlayerModel.SPAWN_POSITION_B, PlayerModel.RIGHT_ORIENTATION);
            return ret;
        }

        public GameModel tick(ActionType actionA, ActionType actionB)
        {
            state.teamA.messages.Clear();
            state.teamB.messages.Clear();

            actionB = resolveEnergy(state.teamB, actionB);
            actionA = resolveEnergy(state.teamA, actionA);


            setDrug(state.teamA, actionA);
            setDrug(state.teamB, actionB);



            bool legalMoveA = applyMove(state.teamA.player, actionA);
            bool legalMoveB = applyMove(state.teamB.player, actionB);

            if (legalMoveA && legalMoveB)
            {
                setPose(state.teamA, actionA);
                setPose(state.teamA, actionB);
                resolveScore(actionA, actionB);
            }
            if (!legalMoveA)
            {
                state.teamB.score += 1;
                observer.scored(state.teamB);
                state.restart = true;

            }
            if (!legalMoveB)
            {
                state.teamB.score += 1;
                observer.scored(state.teamB);
                state.restart = true;
            }

            checkTheEnd();
            checkTheRestart(legalMoveA, legalMoveB);

            state.tick += 1;
            return state;
        }

        private void setDrug(TeamModel team, ActionType a)
        {
            PlayerModel player = team.player;

            if (a.drug > 0)
            {
                if (a == ActionType.PARRY_DRUG)
                {
                    player.parryDistanceSkill += a.drug;
                }
                else if (a == ActionType.RETREAT_DRUG)
                {
                    player.retreatSkill += a.drug;
                }
                else if (a == ActionType.DOUBLE_RETREAT_DRUG)
                {
                    player.doubleRetreatSkill += a.drug;
                }
                else if (a == ActionType.WALK_DRUG)
                {
                    player.walkSkill += a.drug;
                }
                else if (a == ActionType.DOUBLE_WALK_DRUG)
                {
                    player.doubleWalkSkill += a.drug;
                }
                else if (a == ActionType.LUNGE_DRUG)
                {
                    player.lungeDistanceSkill += a.drug;
                }
                else if (a == ActionType.ENERGY_MAX_DRUG)
                {
                    player.energyMax += a.drug;
                }
                else if (a == ActionType.BREAK_DRUG)
                {
                    player.breakSkill += a.drug;
                }

                player.drugs.Add(a);
                observer.doped(player, a);
            }
        }

        private void checkTheEnd()
        {
            bool timeout = state.tick >= MatchModel.MAX_TICK;
            if (state.teamA.player.energy < 0 && state.teamB.player.energy < 0)
            {
                observer.draw();
                theEnd = true;
            }
            else if (state.teamA.player.energy < 0)
            {
                observer.winTheGame();
                theEnd = true;
            }
            else if (state.teamB.player.energy < 0)
            {
                observer.winTheGame();
                theEnd = true;
            }
            else if (state.teamA.score >= TeamModel.SCORE_MAX && state.teamA.score - state.teamB.score >= TeamModel.SCORE_GAP)
            {
                observer.winTheGame();
                theEnd = true;
            }
            else if (state.teamB.score >= TeamModel.SCORE_MAX && state.teamB.score - state.teamA.score >= TeamModel.SCORE_GAP)
            {
                observer.winTheGame();
                theEnd = true;
            }
            else if (timeout && state.teamA.score > state.teamB.score)
            {
                observer.winTheGame();
                theEnd = true;
            }
            else if (timeout && state.teamB.score > state.teamA.score)
            {
                observer.winTheGame();
                theEnd = true;
            }
            else if (timeout)
            {
                observer.draw();
                theEnd = true;
            }
        }

        private void checkTheRestart(bool legalMoveA, bool legalMoveB)
        {
            if (state.teamA.player.touched || state.teamB.player.touched)
            {
                state.restart = true;
            }
            else if (state.teamA.player.position >= state.teamB.player.position)
            {
                observer.collided();
                state.restart = true;
            }
        }

        private ActionType resolveEnergy(TeamModel team, ActionType action)
        {
            int delta = action.energy + ((action == ActionType.BREAK) ? team.player.breakSkill : 0);
            addEnergy(team.player, delta);

            if (team.player.energy < 0)
            {
                team.messages.Add(action.code + " suppressed because of the KO");
                observer.playerTired(team.player);
                return ActionType.SUPPRESSED;
            }
            if (delta != 0)
            {
                team.messages.Add("energy " + (delta >= 0 ? "+" : "") + delta);
            }

            return action;
        }

        private void resolveScore(ActionType actionA, ActionType actionB)
        {
            state.teamA.player.touched = false;
            state.teamB.player.touched = false;
            if (actionA.distance > 0)
            {
                state.teamB.player.touched = isTouched(state.teamA, actionA, state.teamB, actionB);
                if (state.teamB.player.touched)
                {
                    state.teamA.score += 1;
                    observer.scored(state.teamA);
                }
            }
            else if (actionB.distance < 0)
            {
                observer.defended(state.teamB.player, false);
            }

            if (actionB.distance > 0)
            {
                state.teamA.player.touched = isTouched(state.teamB, actionB, state.teamA, actionA);
                if (state.teamA.player.touched)
                {
                    state.teamB.score += 1;
                    observer.scored(state.teamB);
                }
            }
            else if (actionA.distance < 0)
            {
                observer.defended(state.teamA.player, false);
            }
        }

        private bool isTouched(TeamModel striker, ActionType offensiveAction, TeamModel defender, ActionType defenseAction)
        {
            int defenseDistance = (state.teamA.player.posture == state.teamB.player.posture && defenseAction.distance < 0) ? defenseAction.distance + defender.player.parryDistanceSkill : 0;
            int offensiveDistance = offensiveAction.distance + striker.player.lungeDistanceSkill;

            int defenseLength = defender.player.getRelativePosition() - defenseDistance;
            int offensiveLength = striker.player.getRelativePosition() + offensiveDistance;

            if (defenseLength + offensiveLength >= PlayerModel.MAX_POSITION) //TODO PATCH V2: if (defenseLength + offensiveLength >= PlayerModel.MAX_POSITION)
            {
                if (defenseAction.distance < 0)
                {
                    defender.messages.Add(defenseAction.code + "(" + defenseDistance + ")" + " failed");
                    observer.defended(defender.player, false);
                }
                striker.messages.Add(offensiveAction.code + "(" + offensiveDistance + ")" + " touched");
                observer.hit(striker.player, true);

                return true;
            }
            else
            {
                if (defenseAction.distance < 0)
                {
                    defender.messages.Add(defenseAction.code + "(" + defenseDistance + ")" + " succeeded");
                    observer.hit(defender.player, true);
                    addEnergy(defender.player, defenseAction.energyTransfer);
                    addEnergy(striker.player, -defenseAction.energyTransfer);
                }
                striker.messages.Add(offensiveAction.code + "(" + offensiveDistance + ")" + " failed");
                observer.hit(striker.player, false);

                return false;
            }
        }

        private void setPose(TeamModel team, ActionType action)
        {
            if (action == ActionType.BOTTOM_POSTURE || action == ActionType.TOP_POSTURE || action == ActionType.MIDDLE_POSTURE)
            {
                if (action != team.player.posture) team.messages.Add("posture changed:" + action.code);
                else team.messages.Add("posture ignored:" + action.code);
                team.player.posture = action;
            }
        }

        private bool applyMove(PlayerModel player, ActionType action)
        {
            bool ret = true;
            if (action.move != 0)
            {
                int move = player.getMove(action);
                if (move != 0)
                {
                    int p = player.position + player.orientation * move;
                    if (p < PlayerModel.MIN_POSITION || p > PlayerModel.MAX_POSITION)
                    {
                        observer.outside(player);
                        ret = false;
                    }
                    p = Math.Max(p, PlayerModel.MIN_POSITION);
                    p = Math.Min(p, PlayerModel.MAX_POSITION);

                    observer.move(player, p, player.position);
                    player.position = p;
                }
            }
            return ret;
        }


        private void addEnergy(PlayerModel player, int delta)
        {
            byte total = (byte)Math.Min(player.energy + delta, player.energyMax);
            if (total != player.energy)
            {
                observer.energyChanged(player, delta);
                player.energy = total;
            }
        }

        private void initTeam(TeamModel team, int spawn, int orientation)
        {
            team.score = 0;
            team.player.energy = PlayerModel.ENERGY_START;
            team.player.position = spawn;
            team.player.orientation = orientation;
            team.player.reset();
        }

        public GameModel restart()
        {
            state.restart = false;

            observer.move(state.teamA.player, state.teamA.player.position, PlayerModel.SPAWN_POSITION_A);
            state.teamA.player.position = PlayerModel.SPAWN_POSITION_A;
            state.teamA.player.reset();

            observer.move(state.teamB.player, state.teamB.player.position, PlayerModel.SPAWN_POSITION_B);
            state.teamB.player.position = PlayerModel.SPAWN_POSITION_B;
            state.teamB.player.reset();

            state.tick += 1;
            return state;
        }
    }

}
namespace Fencing.Models
{
    public interface MatchObserver
    {
        void playerTired(PlayerModel player);

        void scored(TeamModel team);

        void outside(PlayerModel player);

        void collided();

        void winTheGame();

        void draw();

        void move(PlayerModel player, int from, int to);

        void energyChanged(PlayerModel player, int delta);

        void hit(PlayerModel player, bool succeeded);

        void defended(PlayerModel player, bool succeeded);
        void doped(PlayerModel player, ActionType a);
    }

    public class DummyObserver : MatchObserver
    {
        public void collided()
        {

        }

        public void defended(PlayerModel player, bool succeeded)
        {

        }

        public void doped(PlayerModel player, ActionType a)
        {
        }

        public void draw()
        {

        }

        public void energyChanged(PlayerModel player, int delta)
        {

        }

        public void hit(PlayerModel player, bool succeeded)
        {

        }

        public void move(PlayerModel player, int from, int to)
        {

        }

        public void outside(PlayerModel player)
        {

        }

        public void playerTired(PlayerModel player)
        {

        }

        public void scored(TeamModel team)
        {

        }

        public void winTheGame()
        {

        }
    }
}


namespace Fencing.Models
{

    using System.Collections.Generic;
    using System.Linq;

    public class PlayerModel
    {

        public const int LEFT_ORIENTATION = 1;
        public const int RIGHT_ORIENTATION = -1;

        //Position
        public const int SPAWN_POSITION_A = 200;
        public const int SPAWN_POSITION_B = 300;
        public const int MIN_POSITION = 0;
        public const int MAX_POSITION = 500;

        //Energy
        public const int ENERGY_MAX_SKILL = 20;
        public const int ENERGY_START = 20;

        public int position;
        public int orientation;
        public int energy = ENERGY_START;
        public int energyMax = ENERGY_MAX_SKILL;
        public int walkSkill =0;
        public int doubleWalkSkill=0;
        public int retreatSkill=0;
        public int doubleRetreatSkill=0;
        public int breakSkill = 0;
        public int drugCount = 0;

        public int lungeDistanceSkill = 0;
        public int parryDistanceSkill = 0;
        public ActionType posture;
        public bool touched = false;
        public List<ActionType> drugs = new List<ActionType>();


        public int getRelativePosition()
        {
            if (orientation < 0) return MAX_POSITION - position;
            else return position;
        }

        public int getRelativeOpponentPosition()
        {
            if (orientation > 0) return MAX_POSITION - position;
            else return position;
        }

        public int getMove(ActionType move)
        {
            int gain = 0;
            if (move == ActionType.RETREAT) return move.move + retreatSkill + gain;
            if (move == ActionType.WALK) return move.move + walkSkill + gain;
            if (move == ActionType.DOUBLE_RETREAT) return move.move + doubleRetreatSkill + gain;
            if (move == ActionType.DOUBLE_WALK) return move.move + doubleWalkSkill + gain;
            else return move.move + gain;
        }

        public void reset()
        {
            posture = ActionType.MIDDLE_POSTURE;
            touched = false;
        }

        public PlayerModel Clone(){
            return new PlayerModel(){
                drugs = drugs.ToList(),
                position =position,
                orientation= orientation,
                energy = energy,
                energyMax = energyMax,
                breakSkill = breakSkill,
                walkSkill = walkSkill,
                doubleWalkSkill = doubleWalkSkill,
                retreatSkill = retreatSkill,
                doubleRetreatSkill = doubleRetreatSkill,
                lungeDistanceSkill = lungeDistanceSkill,
                parryDistanceSkill = parryDistanceSkill,
                touched = touched,
                posture =posture,
                drugCount = drugCount

            };
        }
    }
}
namespace Fencing.Models
{
    using System;
    using System.Collections.Generic;
    using System.Linq;

    public class TeamModel
    {

        public const int SCORE_MAX = 20;
        public const int SCORE_GAP = 1;



        public byte score;

        public PlayerModel player = new PlayerModel();

        public List<string> messages = new List<string>();


        public TeamModel Clone(){
            return new TeamModel(){
                messages = new List<string>(),
                player = player.Clone(),
                score = score,
            };
        }
    }
}
namespace TheGame
{
    using CodeInGame;
    using Decision.Genetics;
    using Drawing;
    using Fencing.Models;

    public abstract class TheBaseGameAgent
    {
        protected readonly ILogger Log;
        protected readonly SvgBuilder Svg;
        protected readonly GameModel State;


        public TheBaseGameAgent(ILogger log, SvgBuilder svg, GameModel state)
        {
            this.Log = log;
            this.Svg = svg;
            this.State = state;
        }

        abstract public ActionType Tick();
    }

    public abstract class TheGenomeGameAgent : TheBaseGameAgent
    {
        protected readonly Genome Genome;

        public TheGenomeGameAgent(ILogger log, SvgBuilder svg, GameModel state, Genome Genome) : base(log, svg, state)
        {
            this.Genome = Genome;
        }
    }
}

namespace TheGame
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using CodeInGame;
    using Decision.Genetics;
    using Drawing;
    using Fencing.Models;

    public class TheGameAgent : TheGenomeGameAgent
    {
        public static Genome Seed = new Genome()
        .IntegerArrayGene("BREAK", 25, Enumerable.Range(0, 10).ToArray())
        .IntegerArrayGene("WALK", 25, Enumerable.Range(0, 10).ToArray())
        .FloatDiscretGene("PARRY", Enumerable.Range(0, 10).Select(x => (float)x).ToArray())
        .FloatDiscretGene("DEFENSIVE_RETREAT", Enumerable.Range(0, 10).Select(x => (float)x).ToArray())
        .FloatDiscretGene("LUNGE", Enumerable.Range(0, 10).Select(x => (float)x).ToArray());

        public int BREAK(int d) => (int)Genome.GetIntegerArray("BREAK")[d];
        public int WALK(int d) => (int)Genome.GetIntegerArray("WALK")[d];
        public int PARRY => (int)Genome.GetFloat("PARRY");
        public int DEFENSIVE_RETREAT => (int)Genome.GetFloat("DEFENSIVE_RETREAT");
        public int LUNGE => (int)Genome.GetFloat("LUNGE");

        private static readonly Random rand = new Random();

        private readonly ILogger log;
        private readonly SvgBuilder svg;

        public TheGameAgent(ILogger log, SvgBuilder svg, GameModel State, Genome Genome) : base(log, svg, State, Genome)
        {

        }

        public override ActionType Tick()
        {
            //return GetPossibleActionsPonderation().OneRandomly();
            return GetPossibleActionsPonderation().OrderBy(x => rand.Next()).First();
        }

        public List<ActionType> GetPossibleActionsPonderation()
        {
            var actions = new List<ActionType>();
            var me = new Observation(State.teamA, State.teamB);
            var you = new Observation(State.teamB, State.teamA);

            if (me.canLunge && !you.canParry)
            {
                actions.Add(ActionType.LUNGE);
            }
            else
            {
                if (me.canLunge) AddAction(actions, ActionType.LUNGE, LUNGE);
                if (me.canBreak)AddAction(actions, ActionType.BREAK, BREAK(me.distance));
                if (me.canWalk) AddAction(actions, ActionType.WALK, WALK(me.distance));
                if (me.canRetreat && (!you.canLunge && me.distance > 1)) AddAction(actions, ActionType.PARRY, PARRY);
                if (me.canParry && you.canLunge) AddAction(actions, ActionType.PARRY, PARRY);
            }

            if(!actions.Any())AddAction(actions, ActionType.BREAK, 1);
            return actions;
        }

        private void AddAction(List<ActionType> self, ActionType action, int count)
        {
            for (int i = 0; i < count; i++)
            {
                self.Add(action);
            }
        }
    }
}
namespace TheGame
{
    using CodeInGame;
    using Fencing.Models;

    public class TheGameReader
    {
        protected readonly IReader com;
        private readonly ILogger log;

        public TheGameReader(IReader com, ILogger log)
        {
            this.com = com;
            this.log = log;
        }

        public void Init()
        {

        }

        public GameModel Tick()
        {
            var me = this.com.ReadLine().Split(' ');
            var you = this.com.ReadLine().Split(' ');
            return new GameModel()
            {
                teamA = new TeamModel()
                {
                    score = byte.Parse(me[2]),
                    player = new PlayerModel()
                    {
                        position = int.Parse(me[0]),
                        energy = int.Parse(me[1]),
                        orientation = 1,
                        drugCount = int.Parse(me[3]),
                        energyMax = int.Parse(me[4]),
                        breakSkill = int.Parse(me[5]),
                        walkSkill = int.Parse(me[6]),
                        doubleWalkSkill = int.Parse(me[7]),
                        retreatSkill = int.Parse(me[8]),
                        doubleRetreatSkill = int.Parse(me[9]),
                        lungeDistanceSkill = int.Parse(me[10]),
                        parryDistanceSkill = int.Parse(me[11]),
                    }
                },
                teamB = new TeamModel()
                {
                    score = byte.Parse(you[2]),
                    player = new PlayerModel()
                    {
                        position = int.Parse(you[0]),
                        energy = int.Parse(you[1]),
                        orientation = -1,
                        drugCount = int.Parse(me[3]),
                        energyMax = int.Parse(me[4]),
                        breakSkill = int.Parse(me[5]),
                        walkSkill = int.Parse(me[6]),
                        doubleWalkSkill = int.Parse(me[7]),
                        retreatSkill = int.Parse(me[8]),
                        doubleRetreatSkill = int.Parse(me[9]),
                        lungeDistanceSkill = int.Parse(me[10]),
                        parryDistanceSkill = int.Parse(me[11]),
                    }
                }
            };
        }
    }
}

namespace TheGame
{

    using CodeInGame;
    using Fencing.Models;

    public class TheGameWriter
    {
        protected readonly IWriter com;
        private readonly ILogger log;

        public TheGameWriter(IWriter com, ILogger log)
        {
            this.com = com;
            this.log = log;
        }

        public void Write(ActionType output)
        {
            com.WriteLine(output.code);
        }
    }
}

namespace TheGame
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.Linq;
    using CodeInGame;
    using Decision;
    using Drawing;
    using Fencing.Models;

    public class TheTreeGameAgent : TheBaseGameAgent
    {
        public TheTreeGameAgent(ILogger log, SvgBuilder svg, GameModel state) : base(log, svg, state)
        {

        }
        public override ActionType Tick()
        {
            return TickRandom();
        }

        public ActionType TickRandom()
        {
            Stopwatch sw = Stopwatch.StartNew();
            var evaluation = MyNode.Evaluate(State, 5, TimeSpan.FromMilliseconds(45));
            var leader = (State.teamA.score > State.teamB.score);
            var distance = (State.teamB.player.position - State.teamA.player.position) / ActionType.WALK.move;
            var target = evaluation.ToDictionary(x => x.Key, x => Math.Round(x.Value.AScore * 100 / ((x.Value.BScore+x.Value.AScore) == 0 ? 0.01 : (double)(x.Value.BScore+x.Value.AScore))));
            this.Log.Log($"Leader={leader} distance={distance} ElapsedMilliseconds={sw.ElapsedMilliseconds}");
            foreach (var kv in target)
            {
                this.Log.Log($"{kv.Key.code} S={Math.Round(kv.Value)}%");
            }
            return CommandBag.Winner(target, ActionType.BREAK_DRUG);
        }

        public ActionType TickLogic()
        {
            Stopwatch sw = Stopwatch.StartNew();
            var evaluation = MyNode.Evaluate(State, 5, TimeSpan.FromMilliseconds(40));
            var leader = (State.teamA.score > State.teamB.score);
            var distance = (State.teamB.player.position - State.teamA.player.position) / ActionType.WALK.move;
            var sorted = evaluation.Select(x => new { score = Math.Round(x.Value.AScore * 100 / (x.Value.BScore == 0 ? 0.01 : (double)x.Value.BScore)), action = x.Key }).OrderBy(x => x.score);
            this.Log.Log($"Leader={leader} distance={distance} ElapsedMilliseconds={sw.ElapsedMilliseconds}");
            foreach (var kv in sorted)
            {
                this.Log.Log($"{kv.action.code} S={Math.Round(kv.score)}%");
            }

            if (sorted.Count() == 1)
            {
                return sorted.First().action;
            }
            else if (sorted.Count() == 0)
            {
                return ActionType.BREAK;

            }
            else
            {
                var score = sorted.First().score;
                // FIXME: return sorted.Where(x => !score.Equals(x.score)).Select(x => x.action).ToArray().OneRandomly();
                var l = new List<ActionType>();
                foreach (var a in sorted)
                {
                    if (a.score == score)
                    {
                        l.Add(a.action);
                    }
                }
                return l.OneRandomly();
            }
        }
    }
}

namespace CodeRoyaleMain
{
    using System;
    using System.Threading;
    using CodeInGame;
    using TheGame;

    class Player
    {
        static void Main(string[] args)
        {
            Thread.CurrentThread.CurrentCulture = System.Globalization.CultureInfo.InvariantCulture;
            //League1();
            League23(2);
            //League23(3);
        }

        static void League1(){
            Observation.LEAGUE = 1;
            var com = new Communicator(Console.Out, Console.In, Console.Error);
            var reader = new TheGameReader(com, com);
            var writer = new TheGameWriter(com, com);
            reader.Init();
            var g = TheGameAgent.Seed.Load("BREAK=1,1,5,8,0,7,4,3,4,0,7,1,6,8,3,3,2,2,4,2,0,4,8,0,5;WALK=7,6,3,8,8,7,3,3,3,1,6,7,9,6,5,9,3,5,1,8,3,4,6,3,8;PARRY=0;DEFENSIVE_RETREAT=8;LUNGE=7");
            // Game Loop
            while (true)
            {
                var state = reader.Tick();
                var agent = new TheGameAgent(com, null, state, g);
                var response = agent.Tick();
                writer.Write(response);
            }
        }

        static void League23(int league){
            Observation.LEAGUE = league;
            var com = new Communicator(Console.Out, Console.In, Console.Error);
            var reader = new TheGameReader(com, com);
            var writer = new TheGameWriter(com, com);

            reader.Init();
            // Game Loop
            while (true)
            {
                var state = reader.Tick();
                //var agent = new TheGameAgent(com, null, state, g);
                var agent = new TheTreeGameAgent(com, null, state);
                var response = agent.Tick();
                writer.Write(response);
            }
        }
    }
}
